package zephyr.plugin.core.api.logging.abstracts;

import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.logging.wrappers.MonitorWrapper;


public interface FieldHandler {
  boolean canHandle(Field field, Object container);

  void addField(Logger logger, Object container, Field field, List<MonitorWrapper> wrappers);
}
