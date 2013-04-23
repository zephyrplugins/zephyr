package zephyr.plugin.plotting.internal.heatmap;

import zephyr.plugin.core.api.viewable.ContinuousFunction2D;

public class FunctionSampler {
  private final ContinuousFunction2D function;
  private double minValue = Double.MAX_VALUE;
  private double maxValue = -Double.MAX_VALUE;

  public FunctionSampler(ContinuousFunction2D continuousFunction) {
    this.function = continuousFunction;
  }

  public void resetRange() {
    minValue = Double.MAX_VALUE;
    maxValue = -Double.MAX_VALUE;
  }

  public void updateData(MapData data) {
    Interval xRange = new Interval(function.minX(), function.maxX());
    Interval yRange = new Interval(function.minY(), function.maxY());
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
  }
}
