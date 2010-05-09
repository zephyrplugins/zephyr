package zephyr.plugin.plotting.graphs;

import java.awt.geom.Point2D;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import rlpark.plugin.utils.logger.LabelBuilder;
import zephyr.plugin.common.utils.Colors;
import zephyr.plugin.plotting.plots.Axes;
import zephyr.plugin.plotting.plots.PlotData;
import zephyr.plugin.plotting.plots.PlotOverTime;
import zephyr.plugin.plotting.plots.PlotData.RequestResult;

public class MouseSearch extends Job {

  protected final PlotView plotView;
  protected Label valueLabel;
  private final Point mousePosition = new Point(-1, -1);
  private final Colors colors = new Colors();
  private final PlotOverTime plotOverTime;
  private final PlotData plotData;
  private Axes displayedAxes;
  private RequestResult requestResult = null;

  public MouseSearch(PlotView plotView) {
    super(PlotView.ID + ".mouse.search");
    setPriority(SHORT);
    this.plotView = plotView;
    plotOverTime = plotView.plotOverTime;
    plotData = plotView.plotdata;
  }

  protected void scheduleIFN(int mousePositionX, int mousePositionY) {
    displayedAxes = plotOverTime.getAxes();
    if (displayedAxes == null)
      return;
    if (plotData.isEmpty())
      return;
    mousePosition.x = mousePositionX;
    mousePosition.y = mousePositionY;
    schedule();
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
    Point2D.Double dataMousePosition = displayedAxes.toD(mousePosition);
    requestResult = plotData.search(dataMousePosition);
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
        valueLabel.setText(fieldLabel());
        valueLabel.getParent().pack(true);
        plotView.canvas().setToolTipText(tooltipLabel());
        plotView.canvas().redraw();
      }
    });
  }

  protected String fieldLabel() {
    if (requestResult == null)
      return "";
    String label = requestResult.label;
    int lastPrefix = label.lastIndexOf(LabelBuilder.DefaultSeparator);
    if (lastPrefix > 0 && lastPrefix < label.length() - 1)
      label = label.substring(lastPrefix + 1);
    return String.format("%s: %f [T: %d]", label, requestResult.y, requestResult.time);
  }

  protected String tooltipLabel() {
    if (requestResult == null)
      return "";
    return String.format("%s: %f", requestResult.label, requestResult.y);
  }

  public void createLabelControl(Composite composite) {
    valueLabel = new Label(composite, SWT.NONE);
  }

  public void paintMouse(GC gc) {
    if (requestResult == null || plotView.clockGraphBindings.isEmpty()) {
      valueLabel.setText("");
      plotView.canvas().setToolTipText("");
      return;
    }
    gc.setForeground(colors.color(gc, Colors.COLOR_RED));
    final int halfSize = 2;
    displayedAxes = plotOverTime.getAxes();
    int searchShift = requestResult.history.time - requestResult.timeShift;
    assert (searchShift >= 0);
    Point stickyMousePosition = displayedAxes.toG(requestResult.x - searchShift, requestResult.y);
    if (stickyMousePosition == null) {
      requestResult = null;
      refreshDisplay();
      return;
    }
    gc.drawRectangle(stickyMousePosition.x - halfSize,
                     stickyMousePosition.y - halfSize,
                     halfSize * 2, halfSize * 2);
  }
}
