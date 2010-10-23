package zephyr.plugin.plotting.mousesearch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import zephyr.plugin.core.canvas.Overlay;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.plotting.internal.graphs.PlotView;

public class MouseSearch extends Job implements Overlay {
  protected Label valueLabel;
  protected boolean searchRunning = false;
  private final Point mousePosition = new Point(-1, -1);
  private final Colors colors = new Colors();
  RequestResult requestResult = null;
  private final MouseSearchable mouseSearchable;
  final Control control;

  public MouseSearch(MouseSearchable mouseSearchable, Control control) {
    super(PlotView.ID + ".mouse.search");
    this.mouseSearchable = mouseSearchable;
    this.control = control;
    monitor(control);
  }

  private void monitor(Control control) {
    control.addMouseMoveListener(new MouseMoveListener() {
      @Override
      public void mouseMove(MouseEvent e) {
        scheduleIFN(e.x, e.y);
      }
    });
  }

  protected void scheduleIFN(int mousePositionX, int mousePositionY) {
    searchRunning = true;
    mousePosition.x = mousePositionX;
    mousePosition.y = mousePositionY;
    schedule();
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
    requestResult = mouseSearchable.search(mousePosition);
    refreshDisplay();
    monitor.done();
    return Status.OK_STATUS;
  }

  private void refreshDisplay() {
    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        if (valueLabel.isDisposed())
          return;
        valueLabel.setText(requestResult != null ? requestResult.fieldLabel() : "");
        valueLabel.getParent().pack(true);
        control.setToolTipText(requestResult != null ? requestResult.tooltipLabel() : "");
        control.redraw();
        searchRunning = false;
      }
    });
  }

  public void createLabelControl(Composite composite) {
    valueLabel = new Label(composite, SWT.NONE);
  }

  @Override
  public void drawOverlay(GC gc) {
    if (requestResult == null || mouseSearchable.emptySearch()) {
      valueLabel.setText("");
      control.setToolTipText("");
      return;
    }
    gc.setForeground(colors.color(gc, Colors.COLOR_RED));
    final int halfSize = 2;
    Point stickyMousePosition = requestResult.computeMousePosition();
    gc.drawRectangle(stickyMousePosition.x - halfSize, stickyMousePosition.y - halfSize,
                     halfSize * 2, halfSize * 2);
  }
}
