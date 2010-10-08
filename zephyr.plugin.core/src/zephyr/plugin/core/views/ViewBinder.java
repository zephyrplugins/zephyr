package zephyr.plugin.core.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import zephyr.plugin.core.ZephyrPluginCommon;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.ClockKillable;

public class ViewBinder {
  public Signal<Clock> onClockAdded = new Signal<Clock>();
  protected final Map<Clock, ClockViews> clockToView = new HashMap<Clock, ClockViews>();
  private final Listener<Clock> onClockKilledListener = new Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      ClockViews clockViews = clockToView.get(clock);
      if (clockViews == null)
        return;
      for (SyncView view : clockViews.getViews())
        unbind(clock, view);
    }
  };
  private final ViewProviders viewProviders = new ViewProviders();

  private String findSecondaryID(IWorkbenchPage activePage, String viewID) {
    IViewReference referenceView = null;
    String secondaryID = null;
    int secondaryIndex = 0;
    while (secondaryID == null || referenceView != null) {
      secondaryIndex++;
      secondaryID = String.valueOf(secondaryIndex);
      referenceView = activePage.findViewReference(viewID, secondaryID);
      if (isViewAvailable(referenceView))
        return secondaryID;
    }
    return secondaryID;
  }

  private boolean isViewAvailable(IViewReference referenceView) {
    if (referenceView == null)
      return false;
    if (referenceView.getView(false) == null)
      return true;
    IViewPart view = referenceView.getView(true);
    if (!(view instanceof TimedView))
      return false;
    return ((TimedView) view).canTimedAdded();
  }

  protected TimedView displayView(String viewID) {
    IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (activeWorkbenchWindow == null)
      return null;
    IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
    String secondaryID = findSecondaryID(activePage, viewID);
    IViewPart view = null;
    try {
      view = activePage.showView(viewID, secondaryID, IWorkbenchPage.VIEW_ACTIVATE);
    } catch (PartInitException e) {
      e.printStackTrace();
    }
    if (!(view instanceof TimedView))
      return null;
    return (TimedView) view;
  }

  private void displayAndBindView(final Clock clock, final Object drawn, Object info, final String viewID) {
    if (ZephyrPluginCommon.shuttingDown)
      return;
    final TimedView[] view = new TimedView[1];
    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        view[0] = displayView(viewID);
      }
    });
    if (view[0] == null)
      return;
    view[0].addTimed(drawn, info);
    bind(clock, view[0]);
  }

  protected ClockViews addClock(Clock clock) {
    ClockViews clockViews = new ClockViews(clock);
    clockToView.put(clock, clockViews);
    if (clock instanceof ClockKillable)
      ((ClockKillable) clock).onKill.connect(onClockKilledListener);
    onClockAdded.fire(clock);
    return clockViews;
  }

  protected void removeClock(Clock clock) {
    if (clock instanceof ClockKillable)
      ((ClockKillable) clock).onKill.disconnect(onClockKilledListener);
    clockToView.remove(clock);
  }

  synchronized public void bind(Clock clock, SyncView view) {
    ClockViews clockViews = clockToView.get(clock);
    if (clockViews == null)
      clockViews = addClock(clock);
    clockViews.addView(view);
  }

  synchronized public void bindViews(Clock clock, Object drawn, Object info) {
    List<String> viewIDs = viewProviders.findViews(drawn);
    for (String viewID : viewIDs)
      displayAndBindView(clock, drawn, info, viewID);
  }

  synchronized public void unbind(Clock clock, SyncView view) {
    ClockViews clockViews = clockToView.get(clock);
    if (clockViews == null)
      return;
    clockViews.removeView(view);
    if (clockViews.isEmpty())
      removeClock(clock);
  }

  public boolean isEmpty() {
    return clockToView.isEmpty();
  }

  synchronized public ClockViews getViews(Clock clock) {
    return clockToView.get(clock);
  }

  synchronized public Collection<Clock> getClocks() {
    return new ArrayList<Clock>(clockToView.keySet());
  }

  synchronized public Clock findClock(SyncView view) {
    for (Map.Entry<Clock, ClockViews> entry : clockToView.entrySet())
      if (entry.getValue().contains(view))
        return entry.getKey();
    return null;
  }
}
