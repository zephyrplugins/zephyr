package zephyr.plugin.plotting.privates.view.actions;

import java.util.HashSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.utils.Helper;
import zephyr.plugin.plotting.privates.plots.PlotSelection;
import zephyr.plugin.plotting.privates.traces.Trace;
import zephyr.plugin.plotting.privates.view.PlotView;

public class RemoveAllTracesAction extends Action {
  private final PlotView view;

  public RemoveAllTracesAction(PlotView view) {
    super("Remove All Traces", IAction.AS_PUSH_BUTTON);
    this.view = view;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID, "icons/action_removeall_traces.png"));
  }

  @Override
  public void run() {
    PlotSelection plotSelection = view.plotSelection();
    plotSelection.setCurrentSelection(new HashSet<Trace>());
    ZephyrSync.submitView(view);
  }
}
