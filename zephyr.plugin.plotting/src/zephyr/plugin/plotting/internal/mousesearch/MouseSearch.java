package zephyr.plugin.plotting.internal.mousesearch;

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
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.internal.canvas.Overlay;
import zephyr.plugin.core.internal.utils.Colors;

public class MouseSearch extends Job implements Overlay {
  protected Label valueLabel;
  protected boolean searchRunning = false;
  private final Point mousePosition = new Point(-1, -1);
  private final Colors colors = new Colors();
  RequestResult requestResult = null;
  private final MouseSearchable mouseSearchable;
  final Control control;

  public MouseSearch(MouseSearchable mouseSearchable, Control control) {
    super("mouse.search");
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
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        String searchLabel = requestResult != null ? requestResult.fieldLabel() : "";
        if (validValueLabel()) {
          valueLabel.setText(searchLabel);
          valueLabel.getParent().pack(true);
        }
        String tooltipLabel = requestResult != null ? requestResult.tooltipLabel() : "";
        ZephyrCore.setStatusMessage(tooltipLabel);
        control.setToolTipText(tooltipLabel);
        control.redraw();
        searchRunning = false;
      }
    });
  }

  public void createLabelControl(Composite composite) {
    valueLabel = new Label(composite, SWT.NONE);
  }

  boolean validValueLabel() {
    return valueLabel != null && !valueLabel.isDisposed();
  }

  private void setValueLabel(String text) {
    if (!validValueLabel())
      return;
    valueLabel.setText(text);
  }

  @Override
  public void drawOverlay(GC gc) {
    if (requestResult == null || mouseSearchable.emptySearch()) {
      setValueLabel("");
      control.setToolTipText("");
      return;
    }
    gc.setForeground(colors.color(gc, Colors.COLOR_RED));
    final int halfSize = 2;
    Point stickyMousePosition = requestResult.computeMousePosition();
    gc.drawRectangle(stickyMousePosition.x - halfSize, stickyMousePosition.y - halfSize, halfSize * 2, halfSize * 2);
    if (requestResult.dynamicText())
      setValueLabel(requestResult.fieldLabel());
  }

  public void dispose() {
    colors.dispose();
  }
}
