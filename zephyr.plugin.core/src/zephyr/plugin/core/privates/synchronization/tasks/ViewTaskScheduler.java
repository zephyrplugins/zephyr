package zephyr.plugin.core.privates.synchronization.tasks;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.views.SyncView;

public class ViewTaskScheduler {
  protected final ViewTaskExecutor sequentialExecutor = new ViewTaskExecutor();
  final Map<SyncView, ViewTask> viewTasks = Collections.synchronizedMap(new HashMap<SyncView, ViewTask>());
  public static final Signal<ViewTaskExecutor> onTaskExecuted = new Signal<ViewTaskExecutor>();

  public ViewTask task(SyncView view) {
    ViewTask task = viewTasks.get(view);
    if (task == null) {
      task = new ViewTask(sequentialExecutor, new ViewReference(view));
      viewTasks.put(view, task);
    }
    return task;
  }

  public void disposeView(SyncView view) {
    viewTasks.remove(view);
  }

  public void submitView(SyncView view) {
    ViewTask task = viewTasks.get(view);
    if (task != null)
      task.redrawIFN();
  }

  public void submitView(final SyncView view, final Clock[] clocks) {
    final ViewTask task = viewTasks.get(view);
    if (task == null)
      return;
    schedule(new Runnable() {
      @Override
      public void run() {
        synchronizeRedrawView(task, clocks);
      }
    });
  }

  void synchronizeRedrawView(ViewTask task, Clock[] clocks) {
    for (Clock clock : clocks)
      task.refresh(clock);
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
