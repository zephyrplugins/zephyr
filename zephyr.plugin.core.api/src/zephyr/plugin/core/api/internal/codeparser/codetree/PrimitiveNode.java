package zephyr.plugin.core.api.internal.codeparser.codetree;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.internal.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class PrimitiveNode extends AbstractCodeNode implements MonitorContainerNode {
  private final Monitored monitored;

  public PrimitiveNode(String label, ParentNode parent, Field field, Object container, int level) {
    this(label, parent, createMonitored(field, container), level);
  }

  public PrimitiveNode(String label, ParentNode parent, Monitored monitored, int level) {
    super(label, parent, level);
    this.monitored = monitored;
  }

  public Monitored monitored() {
    return monitored;
  }

  static private Monitored createMonitored(Field field, Object container) {
    if (field.getType().equals(Boolean.TYPE))
      return createBooleanLogged(field, container);
    return createValueLogged(field, container);
  }

  static private Monitored createValueLogged(final Field field, final Object container) {
    return new Monitored() {
      @Override
      public double monitoredValue() {
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

  static private Monitored createBooleanLogged(final Field field, final Object container) {
    return new Monitored() {
      @Override
      public double monitoredValue() {
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

  public double value() {
    return monitored.monitoredValue();
  }

  @Override
  public Monitored createMonitored(String label) {
    return monitored;
  }

  @Override
  public String[] createLabels() {
    return new String[] { "" };
  }

  @Override
  public Monitored[] createMonitored() {
    return new Monitored[] { createMonitored(null) };
  }
}
