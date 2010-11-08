package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCommon;
import zephyr.plugin.core.views.SyncView;

public class ViewTask implements Runnable, Listener<Clock> {
  protected class IsDirtyFlag {
    private boolean isDirty = false;

    synchronized protected boolean isDirty() {
      return isDirty;
    }

    synchronized protected void soil() {
      isDirty = true;
    }

    synchronized protected void clean() {
      isDirty = false;
    }
  }

  final private SyncView view;
  private Future<?> future;
  private final IsDirtyFlag dirtyFlag = new IsDirtyFlag();
  private boolean synchronizationRequired = false;
  private final Set<Clock> clocks = new HashSet<Clock>();
  private final Set<Thread> threads = new HashSet<Thread>();
  private boolean disposed = false;

  protected ViewTask(SyncView view) {
    this.view = view;
    ZephyrPluginCommon.viewBinder().onClockRemoved.connect(this);
  }

  public void addClock(Clock clock) {
    clocks.add(clock);
  }

  @Override
  public void run() {
    try {
      if (disposed)
        return;
      paintView();
      if (synchronizationRequired && (allThreadDead() || allClocksSuspended())) {
        synchronizeWithModel();
        paintView();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void paintView() {
    do {
      dirtyFlag.clean();
      view.repaint();
    } while (!disposed && dirtyFlag.isDirty());
  }

  synchronized private boolean allClocksSuspended() {
    for (Clock clock : clocks)
      if (!ZephyrPluginCommon.control().isSuspended(clock))
        return false;
    return true;
  }

  synchronized private boolean allThreadDead() {
    Iterator<Thread> i = threads.iterator();
    while (i.hasNext())
      if (!i.next().isAlive())
        i.remove();
    return threads.isEmpty();
  }

  synchronized public void synchronizeIFN() {
    threads.add(Thread.currentThread());
    synchronizationRequired = true;
    if (!isDone())
      return;
    synchronizeWithModel();
  }

  private void synchronizeWithModel() {
    synchronizationRequired = false;
    view.synchronize();
  }

  synchronized public void submitIFN(ViewTaskExecutor executor) {
    dirtyFlag.soil();
    if (!isDone())
      return;
    future = executor.submit(this);
  }

  public boolean isTaskForView(SyncView view) {
    return this.view == view;
  }

  public boolean isDone() {
    return future == null || future.isDone();
  }

  @Override
  synchronized public void listen(Clock clock) {
    clocks.remove(clock);
  }

  public void dispose() {
    disposed = true;
    ZephyrPluginCommon.viewBinder().onClockRemoved.disconnect(this);
  }
}
