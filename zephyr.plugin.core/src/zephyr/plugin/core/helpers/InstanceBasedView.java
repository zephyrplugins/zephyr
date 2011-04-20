package zephyr.plugin.core.helpers;

import zephyr.ZephyrSync;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.views.TimedView;

public class InstanceBasedView<T> {
  public interface SingleTimedView<T> extends TimedView {
    void setTimed();
  }

  private T instance = null;
  private Clock clock;
  private CodeNode codeNode;
  private final SingleTimedView<T> view;

  public InstanceBasedView(SingleTimedView<T> view) {
    this.view = view;
  }

  public boolean addTimed(Clock clock, CodeNode codeNode) {
    if (!(codeNode instanceof ClassNode))
      return false;
    ClassNode classNode = (ClassNode) codeNode;
    @SuppressWarnings("unchecked")
    T newInstance = (T) classNode.instance();
    if (instance == newInstance)
      return true;
    if (instance != null)
      return false;
    instance = newInstance;
    this.codeNode = codeNode;
    this.clock = clock;
    view.setTimed();
    return true;
  }

  public CodeNode codeNode() {
    return codeNode;
  }

  public Clock clock() {
    return clock;
  }

  public void removeTimed(Clock clock) {
    assert this.clock == clock;
    instance = null;
    codeNode = null;
    this.clock = null;
    ZephyrSync.unbind(clock, view);
  }

  public T current() {
    return instance;
  }

}
