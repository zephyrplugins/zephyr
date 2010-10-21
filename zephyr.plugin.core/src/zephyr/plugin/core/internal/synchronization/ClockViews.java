package zephyr.plugin.core.internal.synchronization;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCommon;
import zephyr.plugin.core.views.SyncView;

public class ClockViews {
  static final ExecutorService executor = Executors.newFixedThreadPool(3, new ThreadFactory() {
    @Override
    public Thread newThread(Runnable runnable) {
      Thread result = Executors.defaultThreadFactory().newThread(runnable);
      result.setPriority(Thread.MIN_PRIORITY);
      return result;
    }
  });

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
  }

  protected void synchronize() {
    if (ZephyrPluginCommon.shuttingDown)
      return;
    for (Map.Entry<ViewTask, Future<ViewTask>> entry : views.entrySet())
      if (entry.getValue() == null || entry.getValue().isDone())
        entry.setValue(entry.getKey().refresh());
    if (ZephyrPluginCommon.synchronous)
      try {
        executor.awaitTermination(60, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
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
}
