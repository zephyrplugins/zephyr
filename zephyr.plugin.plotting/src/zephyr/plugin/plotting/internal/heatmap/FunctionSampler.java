package zephyr.plugin.plotting.internal.heatmap;

import java.awt.geom.Point2D;
import zephyr.plugin.core.api.viewable.ContinuousFunction2D;

public class FunctionSampler {
  private final ContinuousFunction2D function;
  private double minValue = Double.MAX_VALUE;
  private double maxValue = -Double.MAX_VALUE;
  private final Interval xRange;
  private final Interval yRange;

  public FunctionSampler(ContinuousFunction2D continuousFunction) {
    this.function = continuousFunction;
    xRange = new Interval(function.minX(), function.maxX());
    yRange = new Interval(function.minY(), function.maxY());
  }

  public void resetRange() {
    minValue = Double.MAX_VALUE;
    maxValue = -Double.MAX_VALUE;
  }

  public void updateData(MapData data) {
    try {
      float[][] imageData = data.imageData();
      for (int ax = 0; ax < data.resolutionX; ax++) {
        double x = xRange.min + ((double) ax / data.resolutionX) * xRange.length;
        for (int ay = 0; ay < data.resolutionY; ay++) {
          double y = yRange.min + ((double) ay / data.resolutionY) * yRange.length;
          double value = function.value(x, y);
          minValue = Math.min(minValue, value);
          maxValue = Math.max(maxValue, value);
          imageData[ax][ay] = (float) value;
        }
      }
      data.setRangeValue(new Interval(minValue, maxValue));
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public double valueOf(MapData data, Point2D.Double position) {
    int x = (int) (((position.x - xRange.min) / xRange.length) * data.resolutionX);
    int y = (int) (((position.y - yRange.min) / yRange.length) * data.resolutionY);
    return data.imageData()[x][y];
  }
}
