package zephyr.plugin.core.internal.synchronization.tasks;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.helpers.TimedViews;
import zephyr.plugin.core.views.SyncView;
import zephyr.plugin.core.views.TimedView;

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
    if (view instanceof TimedView)
      return ((TimedView) view).provide(codeNodes);
    return TimedViews.toBooleans(codeNodes, -1);
  }

  public SyncView view() {
    return view;
  }
}
