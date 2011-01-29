package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Display;

import zephyr.plugin.core.api.synchronization.Clock;

public class ViewTask implements Runnable {
  final ViewReference view;
  private Future<?> future;
  private boolean isDirty = false;

  protected ViewTask(ViewReference view) {
    this.view = view;
  }

  @Override
  public void run() {
    try {
      while (isDirty) {
        isDirty = false;
        synchronized (view) {
          view.repaint();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void refreshIFN(ViewTaskExecutor executor) {
    refreshIFN(executor, null, false);
  }

  public void refreshIFN(ViewTaskExecutor executor, Clock clock, boolean synchronize) {
    isDirty = !synchronize;
    if (!isDone())
      return;
    boolean hasSynchronized = false;
    if (synchronize)
      synchronized (view) {
        hasSynchronized = view.synchronize(clock);
      }
    isDirty = isDirty || hasSynchronized;
    if (isDirty && !executor.isShutdown())
      future = executor.submit(this);
  }

  public boolean isDone() {
    return future == null || future.isDone();
  }

  public void disable() {
    isDirty = false;
    while (!isDone())
      Display.getCurrent().readAndDispatch();
  }

  public ViewReference viewRef() {
    return view;
  }
}
