package zephyr.plugin.core.api.viewable;


public interface ContinuousFunction2D {
  double value(double x, double y);

  double minX();

  double maxX();

  double minY();

  double maxY();
}
