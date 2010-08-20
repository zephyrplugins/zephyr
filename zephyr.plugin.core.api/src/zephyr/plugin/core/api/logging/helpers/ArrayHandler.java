package zephyr.plugin.core.api.logging.helpers;

import java.util.List;

import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.logging.wrappers.MonitorWrapper;

public interface ArrayHandler {
  boolean canHandleArray(Object array);

  void addArray(Logger logger, Object array, CollectionLabelBuilder labelBuilder, List<MonitorWrapper> wrappers);
}
