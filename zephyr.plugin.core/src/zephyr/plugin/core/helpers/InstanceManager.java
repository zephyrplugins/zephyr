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
    ZephyrSync.bind(clock, view);
  }

  public boolean[] provide(CodeNode[] codeNode) {
    int displayedIndex = findNodeToDisplay(codeNode);
    if (displayedIndex == -1)
      return TimedViews.toBooleans(codeNode, displayedIndex);
    if (!isDisplayed(codeNode[displayedIndex])) {
      set(codeNode[displayedIndex]);
      view.setInstance();
    }
    return TimedViews.toBooleans(codeNode, displayedIndex);
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
