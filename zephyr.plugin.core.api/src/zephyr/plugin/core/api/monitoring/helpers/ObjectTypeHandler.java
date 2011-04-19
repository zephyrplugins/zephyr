package zephyr.plugin.core.api.monitoring.helpers;

import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.FieldHandler;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.monitoring.wrappers.Wrappers;

public class ObjectTypeHandler implements FieldHandler {

  @Override
  public void addField(DataMonitor logger, Object container, Field field, List<MonitorWrapper> wrappers, int level,
      int levelRequired) {
    Monitor annotation = field.getAnnotation(Monitor.class);
    boolean skipLabel = annotation != null ? annotation.emptyLabel() : false;
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
    Parser.addChildObject(logger, child, localWrappers, level, levelRequired);
    if (!skipLabel)
      logger.labelBuilder().pop();
  }

  @Override
  public boolean canHandle(Field field, Object container) {
    return true;
  }
}
