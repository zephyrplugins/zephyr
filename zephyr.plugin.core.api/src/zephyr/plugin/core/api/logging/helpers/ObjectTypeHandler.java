package zephyr.plugin.core.api.logging.helpers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.logging.abstracts.FieldHandler;
import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.monitoring.DataLogged;

public class ObjectTypeHandler implements FieldHandler {

  @Override
  public void addField(Logger logger, Object container, Field field) {
    DataLogged annotation = field.getAnnotation(DataLogged.class);
    boolean skipLabel = annotation != null ? annotation.skipLabel() : false;
    if (!skipLabel)
      logger.labelBuilder().push(Parser.labelOf(field));
    Object child = null;
    try {
      child = field.get(container);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    Parser.addChildObject(logger, child);
    if (!skipLabel)
      logger.labelBuilder().pop();
  }

  @Override
  public boolean canHandle(Field field, Object container) {
    return true;
  }
}
