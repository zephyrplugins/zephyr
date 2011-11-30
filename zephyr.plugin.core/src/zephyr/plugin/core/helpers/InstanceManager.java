package zephyr.plugin.core.helpers;

import org.eclipse.ui.IMemento;

import zephyr.ZephyrCore;
import zephyr.ZephyrSync;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.async.events.CastedEventListener;
import zephyr.plugin.core.events.ClockEvent;
import zephyr.plugin.core.views.SyncView;

public class InstanceManager<T> {
  public interface InstanceListener<T> extends SyncView {
    void onInstanceSet();

    void onInstanceUnset();
  }

  private T instance = null;
  Clock clock;
  private CodeNode codeNode;
  private final InstanceListener<T> view;

  public InstanceManager(InstanceListener<T> view) {
    this.view = view;
    ZephyrCore.busEvent().register(ClockEvent.RemovedID, new CastedEventListener<ClockEvent>() {
      @Override
      protected void listenEvent(ClockEvent event) {
        if (event.clock() == clock)
          unset();
      }
    });
  }

  public CodeNode codeNode() {
    return codeNode;
  }

  public Clock clock() {
    return clock;
  }

  public void unset() {
    if (instance == null)
      return;
    Clock previousClock = clock;
    ZephyrSync.unbind(previousClock, view);
    view.onInstanceUnset();
    instance = null;
    codeNode = null;
    clock = null;
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
    return instance == null ? 0 : -1;
  }

  public void parseMemento(IMemento memento) {
  }

  public void saveState(IMemento memento) {
  }
}
