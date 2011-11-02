package zephyr.plugin.core.internal.synchronization.tasks;

import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.views.ProvidedView;
import zephyr.plugin.core.views.SyncView;

public class ViewReference {
  private final SyncView view;

  public ViewReference(SyncView view) {
    this.view = view;
  }

  synchronized public boolean synchronize(Clock clock) {
    return view.synchronize(clock);
  }

  synchronized public void repaint() {
    view.repaint();
  }

  public synchronized boolean[] provide(CodeNode[] codeNodes) {
    if (view instanceof ProvidedView)
      return ((ProvidedView) view).provide(codeNodes);
    return CodeTrees.toBooleans(codeNodes, -1);
  }

  public SyncView view() {
    return view;
  }
}
