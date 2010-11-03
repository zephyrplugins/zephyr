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
  private static final ViewTaskScheduler viewTaskScheduler = new ViewTaskScheduler();
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
    clock.onTick.connect(tickListener);
    ViewTaskScheduler.onTaskExecuted.connect(this);
  }

  synchronized protected void synchronize() {
    if (ZephyrPluginCommon.shuttingDown)
      return;
    if (!allTaskDone())
      return;
    for (ViewTask task : viewTasks) {
      task.synchronizeIFN();
      task.submitIFN();
    }
    if (ZephyrPluginCommon.synchronous)
      waitForCompletion();
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
    viewTasks.add(viewTaskScheduler.task(clock, view));
  }

  synchronized public void removeView(SyncView view) {
    viewTasks.remove(viewTaskScheduler.task(clock, view));
  }

  public boolean isEmpty() {
    return viewTasks.isEmpty();
  }

  synchronized public void dispose() {
    clock.onTick.disconnect(tickListener);
    viewTasks.clear();
  }

  @Override
  synchronized public void listen(ViewTaskExecutor eventInfo) {
    this.notify();
  }

  public static void disposeView(SyncView view) {
    viewTaskScheduler.disposeView(view);
  }

  public static void submitView(SyncView view) {
    viewTaskScheduler.submitView(view);
  }
}
