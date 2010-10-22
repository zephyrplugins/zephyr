package zephyr.plugin.core.internal.synchronization;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCommon;
import zephyr.plugin.core.views.SyncView;

public class ClockViews implements Listener<ViewTaskExecutor> {
  static final ViewTaskExecutor executor = new ViewTaskExecutor(3, Thread.MIN_PRIORITY);

  protected final Map<ViewTask, Future<ViewTask>> views = Collections
      .synchronizedMap(new HashMap<ViewTask, Future<ViewTask>>());
  private final Listener<Clock> tickListener = new Listener<Clock>() {
    @Override
    public void listen(Clock eventInfo) {
      synchronize();
    }
  };

  private final Clock clock;

  public ClockViews(Clock clock) {
    this.clock = clock;
    clock.onTick.connect(tickListener);
    executor.onTaskExecuted.connect(this);
  }

  protected void synchronize() {
    if (ZephyrPluginCommon.shuttingDown)
      return;
    for (Map.Entry<ViewTask, Future<ViewTask>> entry : views.entrySet())
      if (entry.getValue() == null || entry.getValue().isDone())
        entry.setValue(entry.getKey().refresh());
    if (ZephyrPluginCommon.synchronous)
      waitForCompletion();
  }

  synchronized private void waitForCompletion() {
    while (!allTaskDone())
      try {
        wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
  }

  private boolean allTaskDone() {
    for (Future<ViewTask> future : views.values())
      if (future != null && !future.isDone())
        return false;
    return true;
  }

  synchronized public void addView(SyncView view) {
    views.put(new ViewTask(view), null);
  }

  synchronized public void removeView(final SyncView view) {
    for (Iterator<Map.Entry<ViewTask, Future<ViewTask>>> i = views.entrySet().iterator(); i.hasNext();) {
      Map.Entry<ViewTask, Future<ViewTask>> entry = i.next();
      if (entry.getKey().isTaskForView(view)) {
        i.remove();
        return;
      }
    }
  }

  public boolean isEmpty() {
    return views.isEmpty();
  }

  public void dispose() {
    clock.onTick.disconnect(tickListener);
    views.clear();
  }

  @Override
  synchronized public void listen(ViewTaskExecutor eventInfo) {
    this.notify();
  }
}
