package zephyr.plugin.core.helpers;

import org.eclipse.ui.IMemento;

import zephyr.ZephyrSync;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.views.SyncView;

public class InstanceManager<T> {
  public interface SetableView extends SyncView {
    void setInstance();

    void unsetInstance();
  }

  private T instance = null;
  private Clock clock;
  private CodeNode codeNode;
  private final SetableView view;

  public InstanceManager(SetableView view) {
    this.view = view;
  }

  public CodeNode codeNode() {
    return codeNode;
  }

  public Clock clock() {
    return clock;
  }

  public void unset() {
    if (clock == null)
      return;
    view.unsetInstance();
    ZephyrSync.unbind(clock, view);
    instance = null;
    codeNode = null;
    clock = null;
  }

  public T current() {
    return instance;
  }

  @SuppressWarnings("unchecked")
  private void set(CodeNode codeNode) {
    if (instance != null)
      unset();
    instance = (T) ((ClassNode) codeNode).instance();
    this.codeNode = codeNode;
    this.clock = CodeTrees.clockOf(codeNode);
    view.setInstance();
    ZephyrSync.bind(clock, view);
  }

  public void drop(CodeNode[] codeNodes) {
    if (!isDisplayed(codeNodes[0]))
      set(codeNodes[0]);
  }

  public boolean[] provide(CodeNode[] codeNodes) {
    int displayedIndex = findNodeToDisplay(codeNodes);
    if (displayedIndex == -1)
      return TimedViews.toBooleans(codeNodes, displayedIndex);
    if (!isDisplayed(codeNodes[displayedIndex]))
      set(codeNodes[displayedIndex]);
    return TimedViews.toBooleans(codeNodes, displayedIndex);
  }

  private boolean isDisplayed(CodeNode codeNode) {
    return this.codeNode == codeNode;
  }

  private int findNodeToDisplay(CodeNode[] codeNodes) {
    for (int i = 0; i < codeNodes.length; i++)
      if (codeNodes[i] == codeNode)
        return i;
    return instance == null ? 0 : -1;
  }

  public void parseMemento(IMemento memento) {
  }

  public void saveState(IMemento memento) {
  }
}
