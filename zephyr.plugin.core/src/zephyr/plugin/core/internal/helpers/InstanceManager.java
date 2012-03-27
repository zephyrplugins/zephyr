package zephyr.plugin.core.internal.helpers;


import org.eclipse.ui.IMemento;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.async.events.CastedEventListener;
import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.EventListener;
import zephyr.plugin.core.internal.events.ClockEvent;
import zephyr.plugin.core.internal.events.CodeStructureEvent;
import zephyr.plugin.core.internal.views.SyncView;
import zephyr.plugin.core.privates.ZephyrPluginCore;
import zephyr.plugin.core.utils.Eclipse;

public class InstanceManager<T> {
  public interface InstanceListener<T> extends SyncView {
    boolean isSupported(CodeNode codeNode);

    void onInstanceSet();

    void onInstanceUnset();
  }

  private T instance = null;
  Clock clock;
  private CodeNode codeNode;
  private final InstanceListener<T> view;
  private String[] loadedPath;
  private final EventListener treeLoadedListener = new EventListener() {
    @Override
    public void listen(Event eventInfo) {
      setCodetree();
    }
  };

  public InstanceManager(InstanceListener<T> view) {
    this.view = view;
    ZephyrSync.busEvent().register(ClockEvent.RemovedID, new CastedEventListener<ClockEvent>() {
      @Override
      protected void listenEvent(ClockEvent event) {
        if (event.clock() == clock)
          unset();
      }
    });
    ZephyrSync.busEvent().register(CodeStructureEvent.ParsedID, treeLoadedListener);
  }

  protected void setCodetree() {
    if (codeNode != null || loadedPath == null)
      return;
    CodeNode loadedCodenode = ZephyrPluginCore.syncCode().findNode(loadedPath);
    if (loadedCodenode == null)
      return;
    if (!view.isSupported(loadedCodenode))
      return;
    set(loadedCodenode);
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
    ZephyrPluginCore.viewScheduler().submitView(view);
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
    loadedPath = Eclipse.loadPath(memento);
    if (loadedPath == null)
      return;
    setCodetree();
  }

  public void saveState(IMemento memento) {
    String[] savedPath = codeNode != null ? codeNode.path() : loadedPath;
    if (savedPath == null)
      return;
    Eclipse.savePath(memento, savedPath);
  }

  public void dispose() {
    ZephyrSync.busEvent().unregister(CodeStructureEvent.ParsedID, treeLoadedListener);
  }
}
