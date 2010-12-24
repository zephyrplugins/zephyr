package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Display;

import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.views.SyncView;

public class ViewTask implements Runnable {
  final private SyncView view;
  private Future<?> future;
  private boolean enabled;
  private boolean isDirty = false;

  protected ViewTask(SyncView view) {
    this.view = view;
    enabled = false;
  }

  @Override
  public void run() {
    try {
      while (isDirty && enabled) {
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
    if (!enabled)
      return;
    isDirty = !synchronize;
    if (!isDone())
      return;
    boolean hasSynchronized = false;
    if (synchronize)
      synchronized (view) {
        hasSynchronized = view.synchronize(clock);
      }
    isDirty = isDirty || hasSynchronized;
    if (isDirty)
      future = executor.submit(this);
  }

  public boolean isDone() {
    return future == null || future.isDone();
  }

  public void enable() {
    enabled = true;
  }

  public void disable() {
    enabled = false;
    while (!isDone())
      Display.getCurrent().readAndDispatch();
  }
}
