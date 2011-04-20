package zephyr.plugin.core.internal.synchronization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IViewReference;

import zephyr.ZephyrSync;
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

  private void bindWithNewView(CodeNode[] codeNodes, ViewFinder viewFinder) {
    CodeNode[] remainToDisplay = codeNodes;
    while (remainToDisplay.length > 0) {
      ViewReference viewRef = viewFinder.provideNewView();
      if (viewRef == null)
        return;
      boolean[] displayed = viewRef.provide(remainToDisplay);
      int nbToDisplay = remainToDisplay.length;
      remainToDisplay = removeDisplayed(displayed, remainToDisplay);
      if (nbToDisplay == remainToDisplay.length)
        break;
    }
  }

  private CodeNode[] bindWithOpenedViews(CodeNode[] codeNodes, ViewFinder viewFinder) {
    CodeNode[] remainToDisplay = codeNodes;
    for (IViewReference reference : viewFinder.existingViews()) {
      ViewReference viewRef = viewFinder.showView(reference);
      if (viewRef == null)
        continue;
      boolean[] displayed = viewRef.provide(remainToDisplay);
      remainToDisplay = removeDisplayed(displayed, remainToDisplay);
      if (remainToDisplay.length == 0)
        break;
    }
    return remainToDisplay;
  }

  private CodeNode[] removeDisplayed(boolean[] displayed, CodeNode[] codeNodes) {
    List<CodeNode> remainToDisplay = new ArrayList<CodeNode>();
    for (int i = 0; i < codeNodes.length; i++)
      if (!displayed[i])
        remainToDisplay.add(codeNodes[i]);
    CodeNode[] result = new CodeNode[remainToDisplay.size()];
    remainToDisplay.toArray(result);
    return result;
  }

  // displayAndBindView need to call the ViewBinder from within
  // a UI thread inside a syncExec. Therefore, this method cannot be
  // synchronized
  public void displayAndBindView(CodeNode[] codeNodes, String viewID) {
    ViewFinder viewFinder = new ViewFinder(viewID);
    CodeNode[] remainToDisplay = bindWithOpenedViews(codeNodes, viewFinder);
    if (remainToDisplay.length == 0)
      return;
    bindWithNewView(codeNodes, viewFinder);
  }

  public void displayAndBindView(CodeNode codeNodes, String viewID) {
    displayAndBindView(new CodeNode[] { codeNodes }, viewID);
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
