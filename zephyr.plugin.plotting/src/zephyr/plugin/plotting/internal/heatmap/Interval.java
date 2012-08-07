package zephyr.plugin.plotting.internal.heatmap;

public class Interval {
  public final double min;
  public final double max;
  public final double length;

  public Interval(double min, double max) {
    this.min = min;
    this.max = max;
    this.length = max - min;
  }

  final public double scale(double value) {
    if (length == 0)
      return 0;
    return Math.min(Math.max((value - min) / length, 0.0), 1.0);
  }

  @Override
  public String toString() {
    return String.format("(%f,%f)", min, max);
  }
}
