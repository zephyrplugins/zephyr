package zephyr.plugin.core.api.logging.helpers;

import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.logging.abstracts.FieldHandler;
import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.logging.abstracts.Monitored;
import zephyr.plugin.core.api.logging.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.logging.wrappers.Wrappers;

public class PrimitiveTypeHandler implements FieldHandler {

  @Override
  public void addField(Logger logger, final Object container, final Field field, List<MonitorWrapper> wrappers) {
    List<MonitorWrapper> localWrappers = Wrappers.getWrappers(field, wrappers);
    if (field.getType().equals(Boolean.TYPE))
      Loggers.addMonitored(logger, Parser.labelOf(field), createBooleanLogged(container, field), localWrappers);
    else
      Loggers.addMonitored(logger, Parser.labelOf(field), createValueLogged(container, field), localWrappers);
  }

  private Monitored createValueLogged(final Object container, final Field field) {
    return new Monitored() {
      @Override
      public double loggedValue(long stepTime) {
        try {
          return field.getDouble(container);
        } catch (Exception e) {
        }
        return 0.0;
      }
    };
  }

  private Monitored createBooleanLogged(final Object container, final Field field) {
    return new Monitored() {
      @Override
      public double loggedValue(long stepTime) {
        try {
          return field.getBoolean(container) ? 1 : 0;
        } catch (Exception e) {
        }
        return 0.0;
      }
    };
  }

  @Override
  public boolean canHandle(Field field, Object container) {
    if ("serialVersionUID".equals(field.getName()))
      return false;
    return field.getType().isPrimitive();
  }
}
