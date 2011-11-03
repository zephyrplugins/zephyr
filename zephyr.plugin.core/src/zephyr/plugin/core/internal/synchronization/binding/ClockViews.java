package zephyr.plugin.core.internal.synchronization.binding;

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

  public ClockViews(Clock clock) {
    this.clock = clock;
    clock.onTick.connect(tickListener);
    ViewTaskScheduler.onTaskExecuted.connect(this);
  }

  protected void synchronize() {
    runnableThread = Thread.currentThread();
    refreshViewsIFN();
    if (ZephyrPluginCore.synchronous())
      waitForCompletion();
  }

  private void refreshViewsIFN() {
    if (!allTaskDone())
      return;
    for (ViewTask task : getViewTasks())
      task.refreshIFN(executor, clock, true);
  }

  ArrayList<ViewTask> getViewTasks() {
    synchronized (viewTasks) {
      return new ArrayList<ViewTask>(viewTasks);
    }
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
    for (ViewTask task : getViewTasks())
      if (!task.isDone())
        return false;
    return true;
  }

  public void addView(SyncView view) {
    ViewTask task = ZephyrPluginCore.viewScheduler().task(view);
    if (!viewTasks.contains(task))
      viewTasks.add(task);
  }

  public void removeView(SyncView view) {
    viewTasks.remove(ZephyrPluginCore.viewScheduler().task(view));
  }

  public boolean isEmpty() {
    return viewTasks.isEmpty();
  }

  public void dispose() {
    clock.onTick.disconnect(tickListener);
    executor.shutdownNow();
    viewTasks.clear();
  }

  @Override
  public void listen(ViewTaskExecutor eventInfo) {
    synchronized (this) {
      notify();
    }
    if (ZephyrPluginCore.control().isSuspended(clock) || runnableThread == null || !runnableThread.isAlive())
      refreshViewsIFN();
  }

  public SyncView[] getViews() {
    ArrayList<ViewTask> tasks = getViewTasks();
    SyncView[] result = new SyncView[tasks.size()];
    for (int i = 0; i < result.length; i++)
      result[i] = tasks.get(i).viewRef().view();
    return result;
  }

  public Clock clock() {
    return clock;
  }
}