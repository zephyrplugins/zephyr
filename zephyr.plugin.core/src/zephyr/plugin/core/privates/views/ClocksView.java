package zephyr.plugin.core.privates.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.EventListener;
import zephyr.plugin.core.internal.async.listeners.UIListener;
import zephyr.plugin.core.internal.events.ClockEvent;
import zephyr.plugin.core.internal.views.SyncView;
import zephyr.plugin.core.internal.views.ViewProvider;
import zephyr.plugin.core.internal.views.ViewWithControl;

public class ClocksView extends ViewPart implements SyncView, ViewWithControl {
  static final public String ViewID = "zephyr.plugin.core.clocksview";

  static public class Provider implements ViewProvider {
    @Override
    public boolean canViewDraw(CodeNode codeNode) {
      if (codeNode instanceof ClockNode)
        return true;
      if (!(codeNode instanceof ClassNode))
        return false;
      return ((ClassNode) codeNode).instance() instanceof Clock;
    }
  }

  private final EventListener removeClockListener = new UIListener() {
    @Override
    protected void listenInUIThread(Event event) {
      if (!isViewActive())
        return;
      removeComposite(((ClockEvent) event).clock());
    }
  };
  private final EventListener addClockListener = new UIListener() {
    @Override
    public void listenInUIThread(Event event) {
      if (!isViewActive())
        return;
      addComposite(((ClockEvent) event).clock());
    }
  };

  private Composite parent;
  private final Map<Clock, ClockComposite> composites = Collections
      .synchronizedMap(new HashMap<Clock, ClockComposite>());

  public ClocksView() {
    ZephyrSync.busEvent().register(ClockEvent.AddedID, addClockListener);
    ZephyrSync.busEvent().register(ClockEvent.RemovedID, removeClockListener);
  }

  boolean isViewActive() {
    return parent != null && !parent.isDisposed();
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
    if (parent != null)
      parent.layout(true, true);
  }

  @Override
  public void dispose() {
    ZephyrSync.busEvent().unregister(ClockEvent.AddedID, addClockListener);
    ZephyrSync.busEvent().unregister(ClockEvent.RemovedID, removeClockListener);
    parent = null;
    for (Clock clock : new ArrayList<Clock>(composites.keySet()))
      removeComposite(clock);
    super.dispose();
  }

  @Override
  public Control control() {
    return parent;
  }
}
