package zephyr.plugin.core.api.logging.helpers;

import zephyr.plugin.core.api.logging.abstracts.Logger;

public interface ArrayHandler {
  boolean canHandleArray(Object array);

  void addArray(Logger logger, Object container, String id, String label);
}
