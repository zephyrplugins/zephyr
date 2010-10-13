package zephyr.plugin.core.api.monitoring.helpers;

import java.util.List;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;

public interface ArrayHandler {
  boolean canHandleArray(Object array);

  void addArray(DataMonitor logger, Object array, CollectionLabelBuilder labelBuilder, List<MonitorWrapper> wrappers, int level, int levelRequired);
}
