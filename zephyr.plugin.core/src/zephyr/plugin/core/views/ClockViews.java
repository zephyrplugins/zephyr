package zephyr.plugin.core.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Display;

import zephyr.plugin.core.ZephyrPluginCommon;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;

public class ClockViews {
  protected final List<SyncView> views = new ArrayList<SyncView>();
  protected List<SyncView> synchronizedViews = new ArrayList<SyncView>();
  protected Set<SyncView> pendingViews = Collections.synchronizedSet(new HashSet<SyncView>());
  private final Listener<Clock> tickListener = new Listener<Clock>() {
    @Override
    public void listen(Clock eventInfo) {
      synchronize();
    }
  };
  private final Runnable asyncRunnable = new Runnable() {
    @Override
    public void run() {
      if (pendingViews == null)
        return;
      for (SyncView view : new ArrayList<SyncView>(pendingViews))
        view.repaint();
      pendingViews.clear();
    }
  };
  private final Runnable syncRunnable = new Runnable() {
    @Override
    public void run() {
      for (SyncView view : synchronizedViews)
        if (view.isDisposed())
          removeView(view);
        else
          view.repaint();
    }
  };

  public ClockViews(Clock clock) {
    clock.onTick.connect(tickListener);
  }

  protected void synchronize() {
    if (ZephyrPluginCommon.shuttingDown)
      return;
    if (!pendingViews.isEmpty())
      return;
    List<SyncView> readyViews = getViews();
    synchronizedViews.clear();
    for (SyncView view : readyViews)
      if (view.synchronize())
        synchronizedViews.add(view);
    if (ZephyrPluginCommon.synchronous)
      syncRedraw();
    else
      asyncRedraw();
  }

  private void asyncRedraw() {
    pendingViews.addAll(synchronizedViews);
    Display.getDefault().asyncExec(asyncRunnable);
  }

  private void syncRedraw() {
    Display.getDefault().syncExec(syncRunnable);
  }

  public synchronized List<SyncView> getViews() {
    return new LinkedList<SyncView>(views);
  }

  synchronized public void addView(SyncView view) {
    views.add(view);
  }

  synchronized public void removeView(final SyncView view) {
    if (!ZephyrPluginCommon.shuttingDown)
      Display.getDefault().asyncExec(new Runnable() {
        @Override
        public void run() {
          view.repaint();
        }
      });
    views.remove(view);
  }

  synchronized public boolean contains(SyncView view) {
    return views.contains(view);
  }

  public boolean isEmpty() {
    return views.isEmpty();
  }
}
