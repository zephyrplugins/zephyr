package zephyr.plugin.plotting.heatmap;

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
    return Math.min(Math.max((value - min) / length, 0.0), 1.0);
  }
}