package zephyr.plugin.plotting.internal.graphs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import zephyr.plugin.core.utils.Helper;
import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;
import zephyr.plugin.plotting.internal.commands.SelectTraces;
import zephyr.plugin.plotting.internal.graphs.PlotView;

public class SelectTracesAction extends Action {
  private final PlotView view;

  public SelectTracesAction(PlotView view) {
    super("Select Traces", IAction.AS_PUSH_BUTTON);
    this.view = view;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrPluginPlotting.PLUGIN_ID, "icons/action_select_traces.png"));
  }

  @Override
  public void run() {
    SelectTraces.selectTraces(view);
  }
}
