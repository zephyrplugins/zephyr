package zephyr.plugin.core.api.internal.codeparser.codetree;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;
import zephyr.plugin.core.api.internal.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class PrimitiveNode extends AbstractCodeNode implements MonitorContainerNode {
  private final double initialValue;
  private final Monitored monitored;

  public PrimitiveNode(String label, ParentNode parent, CodeHook field, Object container, int level) {
    this(label, parent, createMonitored(field, container), level);
  }

  public PrimitiveNode(String label, ParentNode parent, Monitored monitored, int level) {
    super(label, parent, level);
    this.monitored = monitored;
    initialValue = monitored.monitoredValue();
  }

  public Monitored monitored() {
    return monitored;
  }

  static private Monitored createMonitored(CodeHook field, Object container) {
    Monitored monitored;
    if (field.getType().equals(Boolean.TYPE))
      monitored = createBooleanLogged(field, container);
    else
      monitored = createValueLogged(field, container);
    return monitored;
  }

  static private Monitored createValueLogged(final CodeHook hook, final Object container) {
    return new MonitoredWithCodeHook() {
      @Override
      public double monitoredValue() {
        try {
          return hook.getDouble(container);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        return 0;
      }

      @Override
      public CodeHook hook() {
        return hook;
      }
    };
  }

  static private Monitored createBooleanLogged(final CodeHook hook, final Object container) {
    return new MonitoredWithCodeHook() {
      @Override
      public double monitoredValue() {
        try {
          return hook.getBoolean(container) ? 1 : 0;
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        return 0.0;
      }

      @Override
      public CodeHook hook() {
        return hook;
      }
    };
  }

  public double initialValue() {
    return initialValue;
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
