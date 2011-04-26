package zephyr.plugin.plotting.internal.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.monitoring.abstracts.MonitoredDataTraverser;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.helpers.SyncViewDropTarget;
import zephyr.plugin.core.views.DropTargetView;
import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;
import zephyr.plugin.plotting.internal.traces.ClockTracesManager;
import zephyr.plugin.plotting.internal.traces.TraceManager;

public class MonitoredView extends ViewPart implements DropTargetView {
  private List guiList;

  public MonitoredView() {
  }

  @Override
  public void createPartControl(Composite parent) {
    guiList = new List(parent, 0);
    GridLayout gridLayout = new GridLayout(1, false);
    guiList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    parent.setLayout(gridLayout);
    new SyncViewDropTarget(this, parent);
    buildList();
  }

  protected void buildList() {
    ClockTracesManager manager = ZephyrPluginPlotting.tracesManager();
    Collection<TraceManager> traceManagers = manager.traceManagers();
    ArrayList<String> labels = new ArrayList<String>();
    for (TraceManager traceManager : traceManagers)
      labels.addAll(traceManager.labels());
    Collections.sort(labels);
    guiList.removeAll();
    for (String label : labels)
      guiList.add(label);
  }

  @Override
  public void setFocus() {
  }

  @Override
  public boolean isSupported(CodeNode codeNode) {
    return true;
  }

  @Override
  public void drop(CodeNode[] supported) {
    Map<Clock, Collection<CodeNode>> clocks = CodeTrees.sortByClock(supported);
    ClockTracesManager manager = ZephyrPluginPlotting.tracesManager();
    for (Map.Entry<Clock, Collection<CodeNode>> entry : clocks.entrySet()) {
      TraceManager traceManager = manager.traceManager(entry.getKey());
      for (CodeNode codeNode : entry.getValue()) {
        MonitoredDataTraverser traverser = new MonitoredDataTraverser(traceManager);
        CodeTrees.traverse(traverser, codeNode);
      }
    }
    Display.getCurrent().asyncExec(new Runnable() {
      @Override
      public void run() {
        buildList();
      }
    });
  }
}
