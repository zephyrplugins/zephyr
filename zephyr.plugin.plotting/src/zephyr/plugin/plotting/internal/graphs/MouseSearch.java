package zephyr.plugin.plotting.internal.graphs;

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

import zephyr.plugin.core.api.logging.LabelBuilder;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.plotting.internal.plots.PlotData;
import zephyr.plugin.plotting.internal.plots.PlotOverTime;
import zephyr.plugin.plotting.internal.plots.PlotData.RequestResult;
import zephyr.plugin.plotting.plot2d.Axes;

public class MouseSearch extends Job {

  protected final PlotView plotView;
  protected Label valueLabel;
  protected boolean searchRunning = false;
  private final Point mousePosition = new Point(-1, -1);
  private final Colors colors = new Colors();
  private final PlotOverTime plotOverTime;
  private final PlotData plotData;
  private Axes displayedAxes;
  private RequestResult requestResult = null;

  public MouseSearch(PlotView plotView) {
    super(PlotView.ID + ".mouse.search");
    this.plotView = plotView;
    plotOverTime = plotView.plotOverTime;
    plotData = plotView.plotdata;
  }

  protected void scheduleIFN(int mousePositionX, int mousePositionY) {
    searchRunning = true;
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
    requestResult = plotData.search(dataMousePosition, displayedAxes.scaleToDY(1));
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
        searchRunning = false;
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
    return String.format("%s: %f [T: %d]", label, requestResult.y, requestResult.dataAge);
  }

  protected String tooltipLabel() {
    if (requestResult == null)
      return "";
    StringBuilder tooltipLabel = new StringBuilder(String.format("T: %d Val: %f\n%s", requestResult.dataAge,
                                                                 requestResult.y, requestResult.label));
    for (String secondaryLabel : requestResult.secondaryLabels) {
      tooltipLabel.append("\n");
      tooltipLabel.append(secondaryLabel);
    }
    return tooltipLabel.toString();
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
    int timeOffset = requestResult.history.timeInfo.synchronizationTime - requestResult.synchronizationTime;
    assert timeOffset >= 0;
    int currentPositionX = requestResult.x - timeOffset / requestResult.history.timeInfo.period;
    Point stickyMousePosition = displayedAxes.toG(currentPositionX, requestResult.y);
    gc.drawRectangle(stickyMousePosition.x - halfSize,
                     stickyMousePosition.y - halfSize,
                     halfSize * 2, halfSize * 2);
  }

  public long lastResultTime() {
    if (searchRunning)
      return 0;
    if (requestResult == null)
      return Long.MAX_VALUE;
    return requestResult.resultTime.getCurrentMillis();
  }
}
