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


  public void refreshIFN(ViewTaskExecutor executor) {
    refreshIFN(executor, false);
  }

  synchronized public void refreshIFN(ViewTaskExecutor executor, boolean synchronize) {
    isDirty = !synchronize;
    if (!isDone())
      return;
    boolean hasSynchronized = false;
    if (synchronize)
      hasSynchronized = view.synchronize();
    isDirty = isDirty || hasSynchronized;
    if (isDirty)
      future = executor.submit(this);
  }

  public boolean isDone() {
    return future == null || future.isDone();
  }

  public void dispose() {
    disposed = true;
  }
}
