package zephyr.plugin.core.privates.synchronization.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.views.SyncView;
import zephyr.plugin.core.privates.ZephyrPluginCore;
import zephyr.plugin.core.privates.clocks.ClockStat;
import zephyr.plugin.core.privates.synchronization.tasks.ViewTask;

public class ClockViews {
  static public final SynchronizationMode synchronizationMode = new SynchronizationMode();
  private final Listener<Clock> tickListener = new Listener<Clock>() {
    @Override
    public void listen(Clock eventInfo) {
      synchronize(false);
    }
  };
  private final Listener<Clock> terminateListener = new Listener<Clock>() {
    @Override
    public void listen(Clock eventInfo) {
      synchronize(true);
    }
  };
  private final List<ViewTask> viewTasks = new ArrayList<ViewTask>();
  private final Clock clock;
  private final ClockStat clockStat;

  public ClockViews(Clock clock) {
    clockStat = ZephyrPluginCore.clocks().clockStats(clock);
    this.clock = clock;
    synchronizationMode.addClock(clock, clockStat);
    clock.onTick.connect(tickListener);
    clock.onTerminate.connect(terminateListener);
  }

  protected void synchronize(boolean forceSynchronization) {
    clockStat.updateBeforeSynchronization();
    List<Future<?>> futures = synchronizeViews(forceSynchronization);
    synchronizationMode.synchronize(clock, futures);
    clockStat.updateAfterSynchronization();
  }

  synchronized private List<Future<?>> synchronizeViews(boolean forceSynchronization) {
    List<Future<?>> futures = new ArrayList<Future<?>>();
    for (ViewTask task : viewTasks) {
      Future<?> future = forceSynchronization ? task.refresh(clock) : task.refreshIFN(clock);
      if (future != null)
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
    clock.onTerminate.disconnect(terminateListener);
    synchronizationMode.removeClock(clock);
    viewTasks.clear();
  }

  public ClockStat clockStats() {
    return clockStat;
  }
}
