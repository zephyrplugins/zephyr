package zephyr.plugin.plotting.privates.view;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.canvas.BackgroundCanvas;
import zephyr.plugin.core.internal.helpers.SyncViewDropTarget;
import zephyr.plugin.core.internal.views.DropTargetView;
import zephyr.plugin.core.internal.views.ProvidedView;
import zephyr.plugin.plotting.internal.actions.CenterPlotAction;
import zephyr.plugin.plotting.internal.actions.CenterPlotAction.ViewCenterable;
import zephyr.plugin.plotting.internal.actions.SynchronizeAction;
import zephyr.plugin.plotting.internal.mousesearch.MouseSearch;
import zephyr.plugin.plotting.internal.mousesearch.MouseSearchable;
import zephyr.plugin.plotting.internal.mousesearch.RequestResult;
import zephyr.plugin.plotting.privates.plots.PlotData;
import zephyr.plugin.plotting.privates.plots.PlotOverTime;
import zephyr.plugin.plotting.privates.plots.PlotSelection;
import zephyr.plugin.plotting.privates.traces.Trace;
import zephyr.plugin.plotting.privates.traces.Traces;
import zephyr.plugin.plotting.privates.view.actions.AddTracesAction;
import zephyr.plugin.plotting.privates.view.actions.RemoveAllTracesAction;
import zephyr.plugin.plotting.privates.view.actions.SelectTracesAction;

public class PlotView extends ViewPart implements ProvidedView, DropTargetView, ViewCenterable, MouseSearchable {
  final public static String ID = "zephyr.plugin.plotting.view.plot";

  protected final PlotSelection plotSelection;
  protected final PlotOverTime plotOverTime;
  protected final PlotData plotdata;
  protected HistoryLength historyLength;
  protected MouseSearch mouseSearch;
  protected ClockGraphBindings clockGraphBindings;
  private BackgroundCanvas backgroundCanvas;
  private final SynchronizeAction synchronizeAction;

  public PlotView() {
    plotSelection = new PlotSelection();
    plotdata = new PlotData(plotSelection);
    plotOverTime = new PlotOverTime(plotdata);
    historyLength = new HistoryLength(plotdata);
    clockGraphBindings = new ClockGraphBindings(this);
    synchronizeAction = new SynchronizeAction();
  }

  @Override
  public void createPartControl(final Composite parent) {
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    parent.setLayout(gridLayout);
    backgroundCanvas = new BackgroundCanvas(parent, plotOverTime);
    backgroundCanvas.listenControlEvent(this);
    backgroundCanvas.setFillLayout();
    Control canvas = backgroundCanvas.canvas();
    mouseSearch = new MouseSearch(this, canvas);
    backgroundCanvas.addOverlay(mouseSearch);
    createSettingBar(parent);
    setupToolbar(getViewSite().getActionBars().getToolBarManager());
    new SyncViewDropTarget(this, parent);
  }

  private void setupToolbar(IToolBarManager toolBarManager) {
    toolBarManager.add(new CenterPlotAction(this));
    toolBarManager.add(new RemoveAllTracesAction(this));
    toolBarManager.add(new SelectTracesAction(this));
    toolBarManager.add(new AddTracesAction(this));
    toolBarManager.add(synchronizeAction);
  }

  private void createSettingBar(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    RowLayout layout = new RowLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    composite.setLayout(layout);
    layout.center = true;
    historyLength.createPartControl(composite, this);
    mouseSearch.createLabelControl(composite);
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    if (memento == null)
      return;
    historyLength.init(memento);
    plotSelection.init(memento);
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    historyLength.saveState(memento);
    plotSelection.saveState(memento);
  }

  @Override
  public void repaint() {
    if (backgroundCanvas == null)
      return;
    backgroundCanvas.paint();
  }

  @Override
  public boolean synchronize(Clock clock) {
    if (!synchronizeAction.synchronizedData() || backgroundCanvas == null)
      return false;
    return plotdata.synchronize(clock);
  }

  @Override
  public void dispose() {
    plotSelection.dispose();
    clockGraphBindings.unBindAll();
    backgroundCanvas.dispose();
    historyLength.dispose();
    super.dispose();
  }

  public PlotSelection plotSelection() {
    return plotSelection;
  }

  @Override
  public void setFocus() {
  }

  public Control canvas() {
    return backgroundCanvas.canvas();
  }

  @Override
  public void center() {
    plotOverTime.resetAxes(true);
    ZephyrSync.submitView(this);
  }

  @Override
  public RequestResult search(Point mousePosition) {
    return plotdata.search(plotOverTime.getAxes(), mousePosition);
  }

  @Override
  public boolean emptySearch() {
    return plotSelection.isEmpty();
  }

  @Override
  public boolean[] provide(CodeNode[] codeNodes) {
    if (!plotSelection.isEmpty())
      return CodeTrees.toBooleans(codeNodes, -1);
    Set<Trace> traces = new HashSet<Trace>();
    for (CodeNode codeNode : codeNodes)
      traces.addAll(Traces.labelToTraces((MonitorContainerNode) codeNode));
    plotSelection.setCurrentSelection(traces);
    return CodeTrees.toBooleansAsTrue(codeNodes);
  }

  @Override
  public boolean isSupported(CodeNode codeNode) {
    return codeNode instanceof MonitorContainerNode;
  }

  @Override
  public void drop(CodeNode[] supported) {
    Set<Trace> traces = plotSelection.getCurrentTracesSelection();
    for (CodeNode codeNode : supported)
      traces.addAll(Traces.labelToTraces((MonitorContainerNode) codeNode));
    plotSelection.setCurrentSelection(traces);
  }

  public Clock[] clocks() {
    return clockGraphBindings.clocks();
  }
}
