package zephyr.plugin.core.helpers;

import org.eclipse.ui.IMemento;

import zephyr.ZephyrSync;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.views.SyncView;

public class InstanceManager<T> {
  private T instance = null;
  private Clock clock;
  private CodeNode codeNode;
  private final SyncView view;

  public InstanceManager(SyncView view) {
    this.view = view;
  }

  public CodeNode codeNode() {
    return codeNode;
  }

  public Clock clock() {
    return clock;
  }

  public void unset() {
    ZephyrSync.unbind(clock, view);
    instance = null;
    codeNode = null;
    clock = null;
  }

  public T current() {
    return instance;
  }

  public boolean same(CodeNode codeNode) {
    return instance == ((ClassNode) codeNode).instance();
  }

  public boolean isNull() {
    return instance == null;
  }

  @SuppressWarnings("unchecked")
  public void set(Clock clock, CodeNode codeNode) {
    if (instance != null)
      unset();
    instance = (T) ((ClassNode) codeNode).instance();
    this.codeNode = codeNode;
    this.clock = clock;
  }

  public void parseMemento(IMemento memento) {
  }

  public void saveState(IMemento memento) {
  }
}
