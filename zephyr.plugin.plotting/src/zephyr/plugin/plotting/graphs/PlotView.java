package zephyr.plugin.plotting.graphs;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import zephyr.plugin.core.canvas.AbstractCanvasView;
import zephyr.plugin.core.canvas.BackgroundCanvas;
import zephyr.plugin.plotting.ZephyrPluginPlotting;
import zephyr.plugin.plotting.plots.PlotData;
import zephyr.plugin.plotting.plots.PlotOverTime;
import zephyr.plugin.plotting.plots.PlotSelection;
import zephyr.plugin.plotting.traces.ClockTracesManager;
import zephyr.plugin.plotting.traces.TracesSelection.TraceSelector;

public class PlotView extends AbstractCanvasView implements TraceSelector {
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
    traces = ZephyrPluginPlotting.clockTracesManager();
    plotSelection = new PlotSelection(traces);
    plotdata = new PlotData(plotSelection);
    plotOverTime = new PlotOverTime(plotdata);
    historyLength = new HistoryLength(plotdata);
    clockGraphBindings = new ClockGraphBindings(this);
    synchronizeAction = new SynchronizeAction(this);
  }

  @Override
  public void createPartControl(final Composite parent) {
    super.createPartControl(parent);
    backgroundCanvas = new BackgroundCanvas(plotOverTime, canvas);
    mouseSearch = new MouseSearch(this);
    createSettingBar(parent);
    canvas.addMouseMoveListener(new MouseMoveListener() {
      @Override
      public void mouseMove(MouseEvent e) {
        mouseSearch.scheduleIFN(e.x, e.y);
      }
    });
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
  protected void setToolbar(IToolBarManager toolbarManager) {
    synchronizeAction.setChecked(synchronizeData);
    toolbarManager.add(synchronizeAction);
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
    backgroundCanvas.drawNewData();
  }

  @Override
  protected void paint(GC gc) {
    backgroundCanvas.paint(gc);
    mouseSearch.paintMouse(gc);
  }

  @Override
  public boolean synchronize() {
    if (synchronizeData &&
        backgroundCanvas != null &&
        !backgroundCanvas.isDrawing()) {
      plotdata.synchronize();
      return true;
    }
    return false;
  }

  @Override
  public void dispose() {
    super.dispose();
    clockGraphBindings.unBindAll();
  }

  public PlotSelection plotSelection() {
    return plotSelection;
  }

  public PlotOverTime plotOverTime() {
    return plotOverTime;
  }

  public void showDrawingProgress() {
    backgroundCanvas.showProgress();
  }
}
