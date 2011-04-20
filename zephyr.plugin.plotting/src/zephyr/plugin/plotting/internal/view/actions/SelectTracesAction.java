package zephyr.plugin.plotting.internal.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.utils.Helper;
import zephyr.plugin.plotting.internal.commands.SelectTraces;
import zephyr.plugin.plotting.internal.view.PlotView;

public class SelectTracesAction extends Action {
  private final PlotView view;

  public SelectTracesAction(PlotView view) {
    super("Select Traces", IAction.AS_PUSH_BUTTON);
    this.view = view;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID, "icons/action_select_traces.png"));
  }

  @Override
  public void run() {
    SelectTraces.selectTraces(view);
  }
}
