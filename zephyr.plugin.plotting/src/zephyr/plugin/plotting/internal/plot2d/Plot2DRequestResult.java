package zephyr.plugin.plotting.internal.plot2d;

import org.eclipse.swt.graphics.Point;
import zephyr.plugin.plotting.internal.axes.Axes;
import zephyr.plugin.plotting.internal.data.Data2D;
import zephyr.plugin.plotting.internal.mousesearch.RequestResult;

public class Plot2DRequestResult implements RequestResult {
  private final Axes axes;
  private final Data2D data;
  private final int xIndex;
  private double xPosition = 0.0;
  private double yPosition = 0.0;
  private final String xLabel;

  public Plot2DRequestResult(Axes axes, Data2D data, int xIndex) {
    this(axes, data, xIndex, null);
  }

  public Plot2DRequestResult(Axes axes, Data2D data, int xIndex, String xLabel) {
    this.data = data;
    this.xIndex = xIndex;
    this.axes = axes;
    this.xLabel = xLabel;
    refreshDataPosition();
  }

  @Override
  public String tooltipLabel() {
    return fieldLabel();
  }

  @Override
  public String fieldLabel() {
    final String abscisseLabel = xLabel != null ? xLabel : String.valueOf(xPosition);
    return String.format("%s x:%s y:%f", data.label, abscisseLabel, yPosition);
  }

  @Override
  public Point computeMousePosition() {
    refreshDataPosition();
    return new Point(axes.toGX(xPosition), axes.toGY(yPosition));
  }

  private void refreshDataPosition() {
    if (xIndex < 0)
      return;
    xPosition = data.xdata[xIndex];
    yPosition = data.ydata[xIndex];
  }

  @Override
  public boolean dynamicText() {
    return true;
  }
}
