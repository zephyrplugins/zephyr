package zephyr.plugin.core.api.logging.abstracts;

import java.lang.reflect.Field;


public interface FieldHandler {
  boolean canHandle(Field field, Object container);

  void addField(Logger logger, Object container, Field field);
}
