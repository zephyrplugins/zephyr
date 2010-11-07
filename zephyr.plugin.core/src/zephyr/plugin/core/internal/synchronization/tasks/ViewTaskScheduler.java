package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.HashMap;
import java.util.Map;

import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.views.SyncView;

public class ViewTaskScheduler {
  public static final ViewTaskExecutor executor = new ViewTaskExecutor(Thread.MIN_PRIORITY);
  private final Map<SyncView, ViewTask> viewTasks = new HashMap<SyncView, ViewTask>();
  public static final Signal<ViewTaskExecutor> onTaskExecuted = new Signal<ViewTaskExecutor>();

  synchronized public ViewTask task(Clock clock, SyncView view) {
    ViewTask task = viewTasks.get(view);
    if (task == null) {
      task = new ViewTask(view);
      viewTasks.put(view, task);
    }
    task.addClock(clock);
    return task;
  }

  synchronized public void disposeView(SyncView view) {
    ViewTask task = viewTasks.remove(view);
    if (task != null)
      task.dispose();
  }

  public void submitView(SyncView view) {
    ViewTask task = viewTasks.get(view);
    if (task != null)
      task.submitIFN();
  }

  public void adjustCoreThread() {
    int nbThread = Math.max(1, Math.min(10, executor.nbActiveZephyrThread()));
    executor.setCorePoolSize(nbThread);
    executor.setMaximumPoolSize(nbThread);
  }
}
