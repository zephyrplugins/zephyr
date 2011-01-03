package zephyr.plugin.core.internal.synchronization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

import zephyr.plugin.core.Utils;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.views.SyncView;
import zephyr.plugin.core.views.TimedView;

public class ViewBinder {
  public Signal<Clock> onClockAdded = new Signal<Clock>();
  public Signal<Clock> onClockRemoved = new Signal<Clock>();
  protected final Map<Clock, ClockViews> clockToView = Collections.synchronizedMap(new HashMap<Clock, ClockViews>());
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
    return ((TimedView) view).canAddTimed();
  }

  protected TimedView displayView(String viewID) {
    IViewRegistry viewRegistry = PlatformUI.getWorkbench().getViewRegistry();
    IViewDescriptor viewDescriptor = viewRegistry.find(viewID);
    IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (activeWorkbenchWindow == null)
      return null;
    IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
    String secondaryID = viewDescriptor.getAllowMultiple() ? findSecondaryID(activePage, viewID) : null;
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

  private void displayAndBindView(final Clock clock, final Object drawn, final Object info,
      final String viewID) {
    Runnable bindViewRunnable = new Runnable() {
      @Override
      public void run() {
        TimedView view = displayView(viewID);
        if (view == null)
          return;
        view.addTimed(clock, drawn, info);
        bind(clock, view);
      }
    };
    if (Utils.isUIThread())
      bindViewRunnable.run();
    else
      Display.getDefault().syncExec(bindViewRunnable);
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

  // displayAndBindView need to call the ViewBinder from within
  // a UI thread inside a syncExec. Therefore, this method cannot be
  // synchronized
  public void bindViews(Clock clock, Object drawn, Object info) {
    List<String> viewIDs = viewProviders.findViews(drawn);
    for (String viewID : viewIDs)
      displayAndBindView(clock, drawn, info, viewID);
  }

  public void unbind(Clock clock, SyncView view) {
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
    ClockViews.disposeView(view);
  }
}
