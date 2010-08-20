package zephyr.plugin.core.api.logging.helpers;

import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.logging.abstracts.FieldHandler;
import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.logging.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.logging.wrappers.Wrappers;
import zephyr.plugin.core.api.monitoring.DataLogged;

public class ObjectTypeHandler implements FieldHandler {

  @Override
  public void addField(Logger logger, Object container, Field field, List<MonitorWrapper> wrappers) {
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
    List<MonitorWrapper> localWrappers = Wrappers.getWrappers(field, wrappers);
    Parser.addChildObject(logger, child, localWrappers);
    if (!skipLabel)
      logger.labelBuilder().pop();
  }

  @Override
  public boolean canHandle(Field field, Object container) {
    return true;
  }
}
