package zephyr.plugin.core.internal.synchronization;

import java.util.concurrent.Future;

import zephyr.plugin.core.views.SyncView;

public class ViewTask implements Runnable {
  final private SyncView view;

  protected ViewTask(SyncView view) {
    this.view = view;
  }

  @Override
  public void run() {
    try {
      view.repaint();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  public Future<ViewTask> refresh() {
    view.synchronize();
    return (Future<ViewTask>) ClockViews.executor.submit(this);
  }

  public boolean isTaskForView(SyncView view) {
    return this.view == view;
  }
}
