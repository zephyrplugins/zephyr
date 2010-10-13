package zephyr.plugin.core.api.monitoring.abstracts;

import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;


public interface FieldHandler {
  boolean canHandle(Field field, Object container);

  void addField(DataMonitor logger, Object container, Field field, List<MonitorWrapper> wrappers, int level, int levelRequired);
}
