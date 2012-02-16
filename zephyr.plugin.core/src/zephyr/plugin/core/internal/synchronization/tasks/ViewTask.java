package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.concurrent.Future;

import zephyr.plugin.core.api.synchronization.Clock;

public class ViewTask implements Runnable {
  final ViewReference view;
  private Future<?> future;
  private final ViewTaskExecutor executor;

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
    if (pending != null)
      return pending;
    return refresh(clock);
  }

  public Future<?> refresh(Clock clock) {
    synchronized (view) {
      view.synchronize(clock);
    }
    return redraw();
  }

  public ViewReference viewRef() {
    return view;
  }
}
