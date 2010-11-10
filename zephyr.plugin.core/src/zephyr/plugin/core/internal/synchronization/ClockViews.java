package zephyr.plugin.core.internal.synchronization;

import java.util.ArrayList;
import java.util.List;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCommon;
import zephyr.plugin.core.internal.synchronization.tasks.ViewTask;
import zephyr.plugin.core.internal.synchronization.tasks.ViewTaskExecutor;
import zephyr.plugin.core.internal.synchronization.tasks.ViewTaskScheduler;
import zephyr.plugin.core.views.SyncView;

public class ClockViews implements Listener<ViewTaskExecutor> {
  private final Listener<Clock> tickListener = new Listener<Clock>() {
    @Override
    public void listen(Clock eventInfo) {
      synchronize();
    }
  };
  private final ViewTaskExecutor executor = new ViewTaskExecutor(1);
  private final List<ViewTask> viewTasks = new ArrayList<ViewTask>();
  private final Clock clock;
  private Thread runnableThread = null;
  private boolean synchronizationRequired = true;

  public ClockViews(Clock clock) {
    this.clock = clock;
    clock.onTick.connect(tickListener);
    ViewTaskScheduler.onTaskExecuted.connect(this);
  }

  synchronized protected void synchronize() {
    if (ZephyrPluginCommon.shuttingDown)
      return;
    synchronizationRequired = true;
    runnableThread = Thread.currentThread();
    refreshViewsIFN();
    if (ZephyrPluginCommon.synchronous)
      waitForCompletion();
  }

  private void refreshViewsIFN() {
    if (!allTaskDone())
      return;
    synchronizationRequired = false;
    for (ViewTask task : viewTasks)
      task.refreshIFN(executor, true);
  }

  private void waitForCompletion() {
    while (!allTaskDone())
      try {
        wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
  }

  private boolean allTaskDone() {
    for (ViewTask task : viewTasks)
      if (!task.isDone())
        return false;
    return true;
  }

  synchronized public void addView(SyncView view) {
    viewTasks.add(ZephyrPluginCommon.viewScheduler().task(view));
  }

  synchronized public void removeView(SyncView view) {
    viewTasks.remove(ZephyrPluginCommon.viewScheduler().task(view));
  }

  public boolean isEmpty() {
    return viewTasks.isEmpty();
  }

  synchronized public void dispose() {
    clock.onTick.disconnect(tickListener);
    viewTasks.clear();
    executor.shutdown();
  }

  @Override
  synchronized public void listen(ViewTaskExecutor eventInfo) {
    notify();
    if ((ZephyrPluginCommon.control().isSuspended(clock) || !runnableThread.isAlive())
        && synchronizationRequired && allTaskDone())
      refreshViewsIFN();
  }

  public static void disposeView(SyncView view) {
    ZephyrPluginCommon.viewScheduler().disposeView(view);
  }

  public static void submitView(SyncView view) {
    ZephyrPluginCommon.viewScheduler().submitView(view);
  }
}
