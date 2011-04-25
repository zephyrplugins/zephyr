package zephyr.plugin.core.helpers;

import org.eclipse.ui.IMemento;

import zephyr.ZephyrSync;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCore;
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
    ZephyrPluginCore.viewScheduler().schedule(new Runnable() {
      @Override
      public void run() {
        syncUnset();
      }
    });
  }

  void syncUnset() {
    if (clock == null)
      return;
    view.unsetInstance();
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
    if (instance != null)
      syncUnset();
    instance = (T) ((ClassNode) codeNode).instance();
    this.codeNode = codeNode;
    this.clock = CodeTrees.clockOf(codeNode);
    view.setInstance();
    ZephyrSync.bind(clock, view);
  }

  private void asyncSet(final CodeNode codeNode) {
    ZephyrPluginCore.viewScheduler().schedule(new Runnable() {
      @Override
      public void run() {
        set(codeNode);
      }
    });
  }

  public void drop(CodeNode[] codeNodes) {
    if (!isDisplayed(codeNodes[0]))
      asyncSet(codeNodes[0]);
  }

  public boolean[] provide(CodeNode[] codeNodes) {
    int displayedIndex = findNodeToDisplay(codeNodes);
    if (displayedIndex == -1)
      return TimedViews.toBooleans(codeNodes, displayedIndex);
    if (!isDisplayed(codeNodes[displayedIndex]))
      asyncSet(codeNodes[displayedIndex]);
    return TimedViews.toBooleans(codeNodes, displayedIndex);
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
