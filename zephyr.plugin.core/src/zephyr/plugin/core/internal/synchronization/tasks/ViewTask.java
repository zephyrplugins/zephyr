package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.concurrent.Future;

import zephyr.plugin.core.views.SyncView;

public class ViewTask implements Runnable {
  final private SyncView view;
  private Future<?> future;
  private boolean disposed = false;
  private boolean isDirty = false;

  protected ViewTask(SyncView view) {
    this.view = view;
  }

  @Override
  public void run() {
    try {
      if (disposed)
        return;
      while (isDirty) {
        isDirty = false;
        view.repaint();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public boolean refreshIFN(ViewTaskExecutor executor) {
    return refreshIFN(executor, false);
  }

  synchronized public boolean refreshIFN(ViewTaskExecutor executor, boolean synchronize) {
    isDirty = true;
    if (!isDone())
      return false;
    boolean hasSynchronized = false;
    if (synchronize) {
      view.synchronize();
      hasSynchronized = true;
    }
    future = executor.submit(this);
    return hasSynchronized;
  }

  public boolean isDone() {
    return future == null || future.isDone();
  }

  public void dispose() {
    disposed = true;
  }
}
