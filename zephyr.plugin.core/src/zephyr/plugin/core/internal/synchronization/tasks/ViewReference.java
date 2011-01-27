package zephyr.plugin.core.internal.synchronization.tasks;

import zephyr.plugin.core.api.synchronization.Clock;
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

  public synchronized boolean addTimed(Clock clock, Object drawn, Object info) {
    if (view instanceof TimedView)
      return ((TimedView) view).addTimed(clock, drawn, info);
    return false;
  }

  synchronized public void removeTimed(Clock clock) {
    if (view instanceof TimedView)
      ((TimedView) view).removeTimed(clock);
  }

  public SyncView view() {
    return view;
  }
}
