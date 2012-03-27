package zephyr.plugin.plotting.internal.mousesearch;

import org.eclipse.swt.graphics.Point;

public interface MouseSearchable {
  RequestResult search(Point mousePosition);

  boolean emptySearch();
}
