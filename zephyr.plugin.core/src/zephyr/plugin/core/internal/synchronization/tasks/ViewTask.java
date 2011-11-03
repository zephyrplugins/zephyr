package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.concurrent.Future;

import zephyr.plugin.core.api.synchronization.Clock;

public class ViewTask implements Runnable {
  final ViewReference view;
  private Future<?> future;
  private boolean repaintRequired = false;
  private final ViewTaskExecutor executor;

  protected ViewTask(ViewTaskExecutor executor, ViewReference view) {
    this.executor = executor;
    this.view = view;
  }

  @Override
  public void run() {
    try {
      do {
        synchronized (view) {
          repaintRequired = false;
          view.repaint();
        }
      } while (repaintRequired);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void refreshIFN() {
    repaintRequired = true;
    refreshIFN(null, false);
  }

  public Future<?> refreshIFN(Clock clock, boolean synchronize) {
    if (!isDone())
      return future;
    if (synchronize)
      synchronized (view) {
        view.synchronize(clock);
      }
    if (!executor.isShutdown())
      future = executor.submit(this);
    return future;
  }

  public boolean isDone() {
    return future == null || future.isDone();
  }

  public ViewReference viewRef() {
    return view;
  }
}
