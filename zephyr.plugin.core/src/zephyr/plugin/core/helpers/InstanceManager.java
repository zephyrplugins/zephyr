package zephyr.plugin.core.helpers;

import org.eclipse.ui.IMemento;

import zephyr.ZephyrSync;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.views.SyncView;

public class InstanceManager<T> {
  public interface InstanceListener<T> extends SyncView {
    void onInstanceSet();

    void onInstanceUnset();
  }

  private T instance = null;
  private Clock clock;
  private CodeNode codeNode;
  private final InstanceListener<T> view;

  public InstanceManager(InstanceListener<T> view) {
    this.view = view;
  }

  public CodeNode codeNode() {
    return codeNode;
  }

  public Clock clock() {
    return clock;
  }

  public void unset() {
    if (isNull())
      return;
    view.onInstanceUnset();
    Clock previousClock = clock;
    instance = null;
    codeNode = null;
    clock = null;
    ZephyrSync.unbind(previousClock, view);
    ZephyrSync.submitView(view);
  }

  public T current() {
    return instance;
  }

  @SuppressWarnings("unchecked")
  void set(CodeNode codeNode) {
    unset();
    instance = (T) ((ClassNode) codeNode).instance();
    this.codeNode = codeNode;
    this.clock = CodeTrees.clockOf(codeNode);
    view.onInstanceSet();
    ZephyrSync.bind(clock, view);
  }

  public void drop(CodeNode[] codeNodes) {
    if (!isDisplayed(codeNodes[0]))
      set(codeNodes[0]);
  }

  public boolean[] provide(CodeNode[] codeNodes) {
    int displayedIndex = findNodeToDisplay(codeNodes);
    if (displayedIndex == -1)
      return CodeTrees.toBooleans(codeNodes, displayedIndex);
    if (!isDisplayed(codeNodes[displayedIndex]))
      set(codeNodes[displayedIndex]);
    return CodeTrees.toBooleans(codeNodes, displayedIndex);
  }

  private boolean isDisplayed(CodeNode codeNode) {
    return this.codeNode == codeNode;
  }

  private int findNodeToDisplay(CodeNode[] codeNodes) {
    for (int i = 0; i < codeNodes.length; i++)
      if (codeNodes[i] == codeNode)
        return i;
    return isNull() ? 0 : -1;
  }

  public void parseMemento(IMemento memento) {
  }

  public void saveState(IMemento memento) {
  }

  public boolean isNull() {
    return instance == null;
  }
}
