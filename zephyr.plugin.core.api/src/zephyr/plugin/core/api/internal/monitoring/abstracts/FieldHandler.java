package zephyr.plugin.core.api.internal.monitoring.abstracts;

import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.internal.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;


public interface FieldHandler {
  boolean canHandle(Field field, Object container);

  void addField(DataMonitor logger, Object container, Field field, List<MonitorWrapper> wrappers, int level,
      int levelRequired);
}
