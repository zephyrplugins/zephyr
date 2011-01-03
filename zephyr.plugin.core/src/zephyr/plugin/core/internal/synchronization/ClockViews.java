package zephyr.plugin.core.internal.synchronization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCore;
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
  private final ViewTaskExecutor executor = new ViewTaskExecutor(2);
  private final List<ViewTask> viewTasks = Collections.synchronizedList(new ArrayList<ViewTask>());
  private final Clock clock;
  private Thread runnableThread = null;
  private boolean synchronizationRequired = true;

  public ClockViews(Clock clock) {
    this.clock = clock;
    clock.onTick.connect(tickListener);
    ViewTaskScheduler.onTaskExecuted.connect(this);
  }

  protected void synchronize() {
    synchronizationRequired = true;
    runnableThread = Thread.currentThread();
    refreshViewsIFN();
    if (ZephyrPluginCore.synchronous())
      waitForCompletion();
  }

  private void refreshViewsIFN() {
    if (!allTaskDone())
      return;
    synchronizationRequired = false;
    List<ViewTask> tasks;
    synchronized (viewTasks) {
      tasks = new ArrayList<ViewTask>(viewTasks);
    }
    for (ViewTask task : tasks)
      task.refreshIFN(executor, clock, true);
  }

  private void waitForCompletion() {
    while (!allTaskDone())
      try {
        synchronized (this) {
          wait();
        }
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

  public void addView(SyncView view) {
    viewTasks.add(ZephyrPluginCore.viewScheduler().task(view));
  }

  public void removeView(SyncView view) {
    viewTasks.remove(ZephyrPluginCore.viewScheduler().task(view));
  }

  public boolean isEmpty() {
    return viewTasks.isEmpty();
  }

  public void dispose() {
    clock.onTick.disconnect(tickListener);
    executor.shutdown();
    viewTasks.clear();
  }

  @Override
  public void listen(ViewTaskExecutor eventInfo) {
    synchronized (this) {
      notify();
    }
    if ((ZephyrPluginCore.control().isSuspended(clock) || runnableThread == null || !runnableThread.isAlive())
        && synchronizationRequired && allTaskDone())
      refreshViewsIFN();
  }

  public static void disposeView(SyncView view) {
    ZephyrPluginCore.viewScheduler().disposeView(view);
  }

  public static void submitView(SyncView view) {
    ZephyrPluginCore.viewScheduler().submitView(view);
  }
}
