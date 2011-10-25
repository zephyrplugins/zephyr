package zephyr.plugin.plotting.plot2d;

import org.eclipse.swt.graphics.Point;

import zephyr.plugin.plotting.axes.Axes;
import zephyr.plugin.plotting.data.Data2D;
import zephyr.plugin.plotting.mousesearch.RequestResult;

public class Plot2DRequestResult implements RequestResult {
  private final Axes axes;
  private final Data2D data;
  private final int xIndex;
  private double xPosition = 0.0;
  private double yPosition = 0.0;

  public Plot2DRequestResult(Axes axes, Data2D data, int xIndex) {
    this.data = data;
    this.xIndex = xIndex;
    this.axes = axes;
    refreshDataPosition();
  }

  @Override
  public String tooltipLabel() {
    return "";
  }

  @Override
  public String fieldLabel() {
    return String.format("%s x:%f y:%f", data.label, xPosition, yPosition);
  }

  @Override
  public Point computeMousePosition() {
    refreshDataPosition();
    return new Point(axes.toGX(xPosition), axes.toGY(yPosition));
  }

  private void refreshDataPosition() {
    xPosition = data.xdata[xIndex];
    yPosition = data.ydata[xIndex];
  }

  @Override
  public boolean dynamicText() {
    return true;
  }
}
