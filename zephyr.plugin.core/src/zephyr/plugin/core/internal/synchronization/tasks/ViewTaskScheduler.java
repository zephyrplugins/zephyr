package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.HashMap;
import java.util.Map;

import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.views.SyncView;

public class ViewTaskScheduler {
  private final ViewTaskExecutor defaultExecutor = new ViewTaskExecutor(1);
  private final Map<SyncView, ViewTask> viewTasks = new HashMap<SyncView, ViewTask>();
  public static final Signal<ViewTaskExecutor> onTaskExecuted = new Signal<ViewTaskExecutor>();

  synchronized public ViewTask task(SyncView view) {
    ViewTask task = viewTasks.get(view);
    if (task == null) {
      task = new ViewTask(view);
      viewTasks.put(view, task);
    }
    return task;
  }

  synchronized public void disposeView(SyncView view) {
    ViewTask task = viewTasks.remove(view);
    if (task != null)
      task.disable();
  }

  public void submitView(SyncView view) {
    ViewTask task = viewTasks.get(view);
    if (task != null)
      task.refreshIFN(defaultExecutor);
  }

  public void enable(SyncView view) {
    task(view).enable();
  }
}
