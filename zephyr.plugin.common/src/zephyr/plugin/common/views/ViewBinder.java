package zephyr.plugin.common.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import rlpark.plugin.utils.events.Listener;
import rlpark.plugin.utils.events.Signal;
import rlpark.plugin.utils.time.Clock;
import rlpark.plugin.utils.time.ClockKillable;
import zephyr.plugin.common.ZephyrPluginCommon;

public class ViewBinder {
  public Signal<Clock> onClockAdded = new Signal<Clock>();
  private List<TimedView> views = null;
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

  public ViewBinder() {
  }

  protected List<TimedView> createViews() {
    List<TimedView> listeners = new ArrayList<TimedView>();
    IConfigurationElement[] config = Platform.getExtensionRegistry()
        .getConfigurationElementsFor("zephyr.plugin.common.views.timedview");
    for (IConfigurationElement element : config) {
      Object o;
      try {
        o = element.createExecutableExtension("class");
        if (o instanceof TimedView)
          listeners.add((TimedView) o);
      } catch (CoreException e) {
        e.printStackTrace();
      }
    }
    return listeners;
  }

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
    return ((TimedView) view).drawn() == null;
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
    return (TimedView) view;
  }

  private void displayAndBindView(final Clock clock, final Object drawn, final TimedView prototype) {
    if (ZephyrPluginCommon.shuttingDown)
      return;
    final TimedView[] view = new TimedView[1];
    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        view[0] = displayView(prototype.viewID());
        view[0].setTimed(drawn);
      }
    });
    view[0].onDispose().connect(new Listener<SyncView>() {
      @Override
      public void listen(SyncView view) {
        view.onDispose().disconnect(this);
        unbind(clock, view);
      }
    });
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

  synchronized public void bindViews(Clock clock, Object drawn) {
    if (views == null)
      views = createViews();
    for (TimedView view : views)
      if (view.canDraw(drawn))
        displayAndBindView(clock, drawn, view);
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
