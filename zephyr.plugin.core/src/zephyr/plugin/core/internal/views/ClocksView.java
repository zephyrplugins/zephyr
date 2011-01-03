package zephyr.plugin.core.internal.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import zephyr.ZephyrSync;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.views.SyncView;
import zephyr.plugin.core.views.ViewProvider;

public class ClocksView extends ViewPart implements SyncView {
  static final public String ViewID = "zephyr.plugin.core.clocksview";

  static public class Provider implements ViewProvider {
    @Override
    public String viewID() {
      return ViewID;
    }

    @Override
    public boolean canViewDraw(Object drawn) {
      return true;
    }
  }

  private final Listener<Clock> removeClockListener = new Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      removeClock(clock);
    }
  };
  private final Listener<Clock> addClockListener = new Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      addClock(clock);
    }
  };

  private Composite parent;
  private final Map<Clock, ClockComposite> composites = Collections
      .synchronizedMap(new HashMap<Clock, ClockComposite>());

  public ClocksView() {
    ZephyrSync.onClockRemoved().connect(removeClockListener);
    ZephyrSync.onClockAdded().connect(addClockListener);
  }

  protected void addClock(final Clock clock) {
    if (!isViewActive())
      return;
    parent.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        addComposite(clock);
      }
    });
    activateView();
  }

  private void activateView() {
    IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (activeWorkbenchWindow == null)
      return;
    IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
    try {
      activePage.showView(ViewID, null, IWorkbenchPage.VIEW_ACTIVATE);
    } catch (PartInitException e) {
      e.printStackTrace();
    }
  }

  private boolean isViewActive() {
    return parent != null && !parent.isDisposed();
  }

  protected void removeClock(final Clock clock) {
    parent.getDisplay().asyncExec(new Runnable() {
      @Override
      public void run() {
        removeComposite(clock);
      }
    });
  }

  @Override
  public void createPartControl(Composite parent) {
    this.parent = parent;
    GridLayout parentLayout = new GridLayout(1, false);
    parentLayout.marginWidth = 0;
    parentLayout.marginHeight = 0;
    parent.setLayout(parentLayout);
    for (Clock clock : ZephyrSync.getClocks())
      addComposite(clock);
  }

  @Override
  public void setFocus() {
  }

  @Override
  public boolean synchronize(Clock clock) {
    ClockComposite clockComposites = composites.get(clock);
    if (clockComposites != null) {
      clockComposites.synchronize();
      return true;
    }
    Collection<ClockComposite> values;
    synchronized (composites) {
      values = new ArrayList<ClockComposite>(composites.values());
    }
    for (ClockComposite composite : values)
      composite.synchronize();
    return true;
  }

  @Override
  public void repaint() {
    if (!isViewActive())
      return;
    parent.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        repaintComposites();
      }
    });
  }

  protected void repaintComposites() {
    for (ClockComposite composite : composites.values())
      composite.repaint();
  }

  protected void addComposite(Clock clock) {
    ClockComposite clockComposite = new ClockComposite(parent, clock);
    GridData griddata = new GridData(SWT.FILL, SWT.UP, true, false);
    clockComposite.setLayoutData(griddata);
    composites.put(clock, clockComposite);
    ZephyrSync.bind(clock, this);
    parent.layout(true, true);
  }

  protected void removeComposite(Clock clock) {
    ClockComposite clockComposite = composites.remove(clock);
    if (clockComposite != null) {
      clockComposite.dispose();
      ZephyrSync.unbind(clock, this);
    }
  }

  @Override
  public void dispose() {
    ZephyrSync.onClockRemoved().disconnect(removeClockListener);
    ZephyrSync.onClockAdded().disconnect(addClockListener);
    parent = null;
    for (Clock clock : new ArrayList<Clock>(composites.keySet()))
      removeComposite(clock);
    super.dispose();
  }
}
