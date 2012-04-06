package zephyr.plugin.core.api.viewable;

import java.awt.geom.Point2D;

public interface ContinuousFunction2D {
  double value(double x, double y);

  double minX();

  double maxX();

  double minY();

  double maxY();

  Point2D position();
}
