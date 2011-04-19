package zephyr.plugin.core.internal.synchronization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;

import zephyr.ZephyrSync;
import zephyr.plugin.core.Utils;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.synchronization.providers.ViewProviderReference;
import zephyr.plugin.core.internal.synchronization.providers.ViewProviders;
import zephyr.plugin.core.internal.synchronization.tasks.ViewReference;
import zephyr.plugin.core.internal.synchronization.viewfinder.ViewFinder;
import zephyr.plugin.core.views.SyncView;

public class ViewBinder {
  public Signal<Clock> onClockAdded = new Signal<Clock>();
  public Signal<Clock> onClockRemoved = new Signal<Clock>();
  protected final Map<Clock, ClockViews> clockToView = Collections.synchronizedMap(new HashMap<Clock, ClockViews>());
  private final ViewProviders viewProviders = new ViewProviders();

  private void bindWithNewView(Clock clock, CodeNode codeNode, ViewFinder viewFinder) {
    ViewReference viewRef = viewFinder.provideNewView();
    if (viewRef != null && viewRef.addTimed(clock, codeNode))
      uiRunBindView(clock, viewRef.view());
  }

  private boolean bindWithOpenedViews(Clock clock, CodeNode codeNode, ViewFinder viewFinder) {
    for (IViewReference reference : viewFinder.existingViews()) {
      ViewReference view = viewFinder.showView(reference);
      if (view != null && view.addTimed(clock, codeNode)) {
        uiRunBindView(clock, view.view());
        return true;
      }
    }
    return false;
  }

  // displayAndBindView need to call the ViewBinder from within
  // a UI thread inside a syncExec. Therefore, this method cannot be
  // synchronized
  public void displayAndBindView(Clock clock, CodeNode codeNode, String viewID) {
    ViewFinder viewFinder = new ViewFinder(viewID);
    boolean binded = bindWithOpenedViews(clock, codeNode, viewFinder);
    if (binded)
      return;
    bindWithNewView(clock, codeNode, viewFinder);
  }

  private void uiRunBindView(final Clock clock, final SyncView view) {
    Runnable bindViewRunnable = new Runnable() {
      @Override
      public void run() {
        bind(clock, view);
      }
    };
    if (Utils.isUIThread())
      bindViewRunnable.run();
    else
      Display.getDefault().asyncExec(bindViewRunnable);
  }

  public List<ViewProviderReference> findViewProviders(CodeNode codeNode) {
    return viewProviders.findViews(codeNode);
  }

  public ClockViews addClock(Clock clock) {
    ClockViews clockViews = clockToView.get(clock);
    if (clockViews != null)
      return clockViews;
    clockViews = new ClockViews(clock);
    clockToView.put(clock, clockViews);
    onClockAdded.fire(clock);
    return clockViews;
  }

  public void removeClock(Clock clock) {
    ClockViews clockViews = clockToView.remove(clock);
    if (clockViews != null) {
      for (SyncView view : clockViews.getViews())
        ZephyrSync.unbind(clock, view);
      onClockRemoved.fire(clock);
      clockViews.dispose();
    }
  }

  public void bind(Clock clock, SyncView view) {
    ClockViews clockViews = clockToView.get(clock);
    if (clockViews == null)
      clockViews = addClock(clock);
    clockViews.addView(view);
  }

  public void unbind(Clock clock, SyncView view) {
    ZephyrPluginCore.viewScheduler().scheduleRemoveTimed(view, clock);
    ClockViews clockViews = clockToView.get(clock);
    if (clockViews == null)
      return;
    clockViews.removeView(view);
  }

  public boolean isEmpty() {
    return clockToView.isEmpty();
  }

  public Collection<Clock> getClocks() {
    synchronized (clockToView) {
      return new ArrayList<Clock>(clockToView.keySet());
    }
  }

  public void disposeView(SyncView view) {
    synchronized (clockToView) {
      for (Map.Entry<Clock, ClockViews> entry : clockToView.entrySet())
        entry.getValue().removeView(view);
    }
    ZephyrPluginCore.viewScheduler().disposeView(view);
  }
}
