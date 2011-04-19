package zephyr.plugin.core.api.codeparser.codetree;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class PrimitiveFieldNode extends AbstractCodeNode implements MonitorContainer {
  final Object container;
  final Field field;

  public PrimitiveFieldNode(String label, ClassNode parent, Field field) {
    super(label, parent);
    this.field = field;
    this.container = parent.instance();
  }

  public Field field() {
    return field;
  }

  public Object container() {
    return container;
  }

  private Monitored createValueLogged() {
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

  private Monitored createBooleanLogged() {
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
  public void addToMonitor(DataMonitor monitor) {
    Monitored monitored = field.getType().equals(Boolean.TYPE) ? createBooleanLogged() : createValueLogged();
    monitor.add(label(), monitored);
  }
}
