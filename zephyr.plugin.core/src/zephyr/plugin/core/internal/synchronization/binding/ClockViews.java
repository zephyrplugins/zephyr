package zephyr.plugin.core.internal.synchronization.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.synchronization.tasks.ViewTask;
import zephyr.plugin.core.views.SyncView;

public class ClockViews {
  static public final SynchronizationMode synchronizationMode = new SynchronizationMode();
  private final Listener<Clock> tickListener = new Listener<Clock>() {
    @Override
    public void listen(Clock eventInfo) {
      synchronize();
    }
  };
  private final List<ViewTask> viewTasks = new ArrayList<ViewTask>();
  private final Clock clock;

  public ClockViews(Clock clock) {
    this.clock = clock;
    synchronizationMode.addClock(clock);
    clock.onTick.connect(tickListener);
  }

  protected void synchronize() {
    List<Future<?>> futures = synchronizeViews();
    synchronizationMode.synchronize(clock, futures);
  }

  synchronized private List<Future<?>> synchronizeViews() {
    List<Future<?>> futures = new ArrayList<Future<?>>();
    for (ViewTask task : viewTasks) {
      Future<?> future = task.refreshIFN(clock);
      assert future != null;
      futures.add(future);
    }
    return futures;
  }

  synchronized public void addView(SyncView view) {
    ViewTask task = ZephyrPluginCore.viewScheduler().task(view);
    if (!viewTasks.contains(task))
      viewTasks.add(task);
  }

  synchronized public void removeView(SyncView view) {
    viewTasks.remove(ZephyrPluginCore.viewScheduler().task(view));
  }

  public Clock clock() {
    return clock;
  }

  synchronized public void dispose() {
    clock.onTick.disconnect(tickListener);
    synchronizationMode.removeClock(clock);
    viewTasks.clear();
  }
}
