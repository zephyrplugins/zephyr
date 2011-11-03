package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.HashMap;
import java.util.Map;

import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.views.SyncView;

public class ViewTaskScheduler {
  protected final ViewTaskExecutor sequentialExecutor = new ViewTaskExecutor();
  private final Map<SyncView, ViewTask> viewTasks = new HashMap<SyncView, ViewTask>();
  public static final Signal<ViewTaskExecutor> onTaskExecuted = new Signal<ViewTaskExecutor>();

  synchronized public ViewTask task(SyncView view) {
    ViewTask task = viewTasks.get(view);
    if (task == null) {
      task = new ViewTask(sequentialExecutor, new ViewReference(view));
      viewTasks.put(view, task);
    }
    return task;
  }

  synchronized public void disposeView(SyncView view) {
    viewTasks.remove(view);
  }

  public void submitView(SyncView view) {
    ViewTask task = viewTasks.get(view);
    if (task != null)
      task.refreshIFN();
  }

  public void schedule(Runnable runnable) {
    if (sequentialExecutor.isShutdown())
      return;
    sequentialExecutor.execute(runnable);
  }

  public void dispose() {
    sequentialExecutor.shutdownNow();
  }
}
