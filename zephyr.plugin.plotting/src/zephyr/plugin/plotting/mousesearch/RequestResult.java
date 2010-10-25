package zephyr.plugin.plotting.mousesearch;

import org.eclipse.swt.graphics.Point;

public interface RequestResult {
  String tooltipLabel();

  String fieldLabel();

  Point computeMousePosition();

  boolean dynamicText();
}
