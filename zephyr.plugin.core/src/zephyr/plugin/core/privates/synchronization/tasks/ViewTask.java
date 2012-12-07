package zephyr.plugin.core.privates.synchronization.tasks;

import java.util.concurrent.Future;
import zephyr.plugin.core.api.synchronization.Clock;

public class ViewTask implements Runnable {
  final ViewReference view;
  private Future<?> future;
  private final ViewTaskExecutor executor;
  private Clock lastSyncClock = null;

  protected ViewTask(ViewTaskExecutor executor, ViewReference view) {
    this.executor = executor;
    this.view = view;
  }

  @Override
  public void run() {
    try {
      synchronized (view) {
        view.repaint();
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private Future<?> isDone() {
    Future<?> current = future;
    if (current != null && !current.isDone())
      return current;
    return null;
  }

  public Future<?> redrawIFN() {
    Future<?> pending = isDone();
    if (pending != null)
      return pending;
    future = null;
    return redraw();
  }

  private Future<?> redraw() {
    if (!executor.isShutdown())
      future = executor.submit(this);
    return future;
  }

  public Future<?> refreshIFN(Clock clock) {
    Future<?> pending = isDone();
    if (pending != null && lastSyncClock == clock)
      return pending;
    return refresh(clock);
  }

  public Future<?> refresh(Clock clock) {
    lastSyncClock = clock;
    synchronized (view) {
      if (clock.acquireData()) {
        try {
          view.synchronize(clock);
        } catch (Throwable t) {
          t.printStackTrace();
        }
        clock.releaseData();
      }
      lastSyncClock = null;
    }
    return redraw();
  }

  public ViewReference viewRef() {
    return view;
  }
}
