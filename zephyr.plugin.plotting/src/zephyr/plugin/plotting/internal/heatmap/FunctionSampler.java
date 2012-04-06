package zephyr.plugin.plotting.internal.heatmap;

import zephyr.plugin.core.api.viewable.ContinuousFunction2D;

public class FunctionSampler {
  private final ContinuousFunction2D function;

  public FunctionSampler(ContinuousFunction2D continuousFunction) {
    this.function = continuousFunction;
  }

  public void updateData(MapData data) {
    Interval xRange = new Interval(function.minX(), function.maxX());
    Interval yRange = new Interval(function.minY(), function.maxY());
    double minValue = Double.MAX_VALUE;
    double maxValue = -Double.MAX_VALUE;
    float[][] imageData = data.imageData();
    for (int ax = 0; ax < data.resolution; ax++) {
      double x = xRange.min + ((double) ax / data.resolution) * xRange.length;
      for (int ay = 0; ay < data.resolution; ay++) {
        double y = yRange.min + ((double) ay / data.resolution) * yRange.length;
        double value = function.value(x, y);
        minValue = Math.min(minValue, value);
        maxValue = Math.max(maxValue, value);
        imageData[ax][ay] = (float) value;
      }
    }
    data.setRangeValue(new Interval(minValue, maxValue));
  }

}
