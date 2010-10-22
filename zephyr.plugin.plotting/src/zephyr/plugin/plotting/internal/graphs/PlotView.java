package zephyr.plugin.plotting.internal.graphs;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import zephyr.ZephyrSync;
import zephyr.plugin.core.canvas.BackgroundCanvas;
import zephyr.plugin.core.canvas.Overlay;
import zephyr.plugin.core.canvas.Views;
import zephyr.plugin.core.views.SyncView;
import zephyr.plugin.plotting.internal.plots.PlotData;
import zephyr.plugin.plotting.internal.plots.PlotOverTime;
import zephyr.plugin.plotting.internal.plots.PlotSelection;
import zephyr.plugin.plotting.internal.traces.ClockTracesManager;
import zephyr.plugin.plotting.internal.traces.TracesSelection.TraceSelector;

public class PlotView extends ViewPart implements TraceSelector, SyncView, Overlay {
  final public static String ID = "zephyr.plugin.plotting.view.plot";
  final private static String SelectionTypeKey = "selection";

  protected final PlotSelection plotSelection;
  protected final PlotOverTime plotOverTime;
  protected final PlotData plotdata;
  protected HistoryLength historyLength;
  protected MouseSearch mouseSearch;
  protected ClockGraphBindings clockGraphBindings;
  protected boolean synchronizeData = true;
  private BackgroundCanvas backgroundCanvas;
  private final ClockTracesManager traces;
  private final SynchronizeAction synchronizeAction;

  public PlotView() {
    traces = ClockTracesManager.manager();
    plotSelection = new PlotSelection(traces);
    plotdata = new PlotData(plotSelection);
    plotOverTime = new PlotOverTime(plotdata);
    historyLength = new HistoryLength(plotdata);
    clockGraphBindings = new ClockGraphBindings(this);
    synchronizeAction = new SynchronizeAction(this);
  }

  @Override
  public void createPartControl(final Composite parent) {
    backgroundCanvas = new BackgroundCanvas(parent, plotOverTime);
    Views.setLayoutData(backgroundCanvas.canvas());
    backgroundCanvas.addOverlay(this);
    mouseSearch = new MouseSearch(this);
    createSettingBar(parent);
    backgroundCanvas.canvas().addMouseMoveListener(new MouseMoveListener() {
      @Override
      public void mouseMove(MouseEvent e) {
        mouseSearch.scheduleIFN(e.x, e.y);
      }
    });
    synchronizeAction.setChecked(synchronizeData);
    getViewSite().getActionBars().getToolBarManager().add(synchronizeAction);
  }

  private void createSettingBar(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    RowLayout layout = new RowLayout();
    composite.setLayout(layout);
    layout.center = true;
    historyLength.createPartControl(composite, this);
    mouseSearch.createLabelControl(composite);
  }

  private Set<String> loadInitialSelection(IMemento memento) {
    IMemento[] selectionMementos = memento.getChildren(SelectionTypeKey);
    Set<String> traceLabels = new LinkedHashSet<String>();
    for (IMemento selectionMemento : selectionMementos)
      traceLabels.add(selectionMemento.getID());
    return traceLabels;
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    if (memento == null)
      return;
    historyLength.init(memento);
    plotSelection.init(loadInitialSelection(memento));
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    historyLength.saveState(memento);
    Set<String> selectedLabels = plotSelection.getLabelsToSave();
    if (selectedLabels == null)
      return;
    for (String label : selectedLabels)
      memento.createChild(SelectionTypeKey, label);
  }

  @Override
  public void repaint() {
    backgroundCanvas.paint();
  }

  @Override
  public void drawOverlay(GC gc) {
    mouseSearch.paintMouse(gc);
  }

  @Override
  public void synchronize() {
    if (synchronizeData && backgroundCanvas != null)
      plotdata.synchronize();
  }

  @Override
  public void dispose() {
    ZephyrSync.disposeView(this);
    super.dispose();
    clockGraphBindings.unBindAll();
    backgroundCanvas.dispose();
  }

  public PlotSelection plotSelection() {
    return plotSelection;
  }

  public PlotOverTime plotOverTime() {
    return plotOverTime;
  }

  @Override
  public void setFocus() {
  }

  public Control canvas() {
    return backgroundCanvas.canvas();
  }
}
