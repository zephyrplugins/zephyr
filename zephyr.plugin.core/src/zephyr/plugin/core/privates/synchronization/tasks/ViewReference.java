package zephyr.plugin.core.privates.synchronization.tasks;

import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.views.ProvidedView;
import zephyr.plugin.core.internal.views.SyncView;

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
