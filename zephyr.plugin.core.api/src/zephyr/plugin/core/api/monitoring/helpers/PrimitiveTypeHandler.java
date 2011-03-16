package zephyr.plugin.core.api.monitoring.helpers;

import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.FieldHandler;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.monitoring.wrappers.Wrappers;

public class PrimitiveTypeHandler implements FieldHandler {

  @Override
  public void addField(DataMonitor logger, final Object container, final Field field, List<MonitorWrapper> wrappers,
      int level, int levelRequired) {
    List<MonitorWrapper> localWrappers = Wrappers.getWrappers(field, wrappers);
    if (field.getType().equals(Boolean.TYPE))
      Loggers.addMonitored(logger, Parser.labelOf(field), createBooleanLogged(container, field), localWrappers, level);
    else
      Loggers.addMonitored(logger, Parser.labelOf(field), createValueLogged(container, field), localWrappers, level);
  }

  private Monitored createValueLogged(final Object container, final Field field) {
    return new Monitored() {
      @Override
      public double monitoredValue(long stepTime) {
        try {
          return field.getDouble(container);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        return 0;
      }
    };
  }

  private Monitored createBooleanLogged(final Object container, final Field field) {
    return new Monitored() {
      @Override
      public double monitoredValue(long stepTime) {
        try {
          return field.getBoolean(container) ? 1 : 0;
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
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
