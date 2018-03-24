package zephyr.plugin.core.privates.synchronization.tasks;

import java.util.concurrent.Future;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.privates.synchronization.Syncins;

public class ViewTask implements Runnable {
  final Syncins<ViewReference> syncView;
  private Future<?> future;
  private final ViewTaskExecutor executor;

  protected ViewTask(ViewTaskExecutor executor, ViewReference view) {
    this.executor = executor;
    this.syncView = new Syncins<ViewReference>(view);
  }

  @Override
  public void run() {
    Syncins<ViewReference>.Handle viewHandle = syncView.acquire();
    if (viewHandle == null)
      return;
    try {
      viewHandle.h().repaint();
    } catch (Throwable t) {
      t.printStackTrace();
    }
    viewHandle.release();
  }

  private Future<?> isDone() {
    Future<?> current = future;
    if (current != null && !current.isDone())
      return current;
    return null;
  }

  public Future<?> redrawIFN() {
    return redraw();
  }

  private Future<?> redraw() {
    if (!executor.isShutdown())
      future = executor.submit(this);
    return future;
  }

  public Future<?> refreshIFN(Clock clock) {
    Future<?> pending = isDone();
    if (pending != null)
      return pending;
    Syncins<ViewReference>.Handle viewHandle = syncView.tryAcquire();
    if (viewHandle == null)
      return null;
    return refresh(clock, viewHandle);
  }

  public Future<?> refresh(Clock clock) {
    return refresh(clock, syncView.acquire());
  }

  private Future<?> refresh(Clock clock, Syncins<ViewReference>.Handle viewHandle) {
    boolean synced = false;
    if (clock.acquireData()) {
      try {
        synced = viewHandle.h().synchronize(clock);
      } catch (Throwable t) {
        t.printStackTrace();
      }
      clock.releaseData();
    }
    viewHandle.release();
    if (!synced)
      return null;
    return redraw();
  }

  public ViewReference viewRef() {
    return syncView.instance();
  }
}
