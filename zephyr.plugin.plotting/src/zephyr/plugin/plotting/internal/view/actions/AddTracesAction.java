package zephyr.plugin.plotting.internal.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.utils.Helper;
import zephyr.plugin.plotting.internal.commands.AddTraces;
import zephyr.plugin.plotting.internal.view.PlotView;

public class AddTracesAction extends Action {
  private final PlotView view;

  public AddTracesAction(PlotView view) {
    super("Add Traces", IAction.AS_PUSH_BUTTON);
    this.view = view;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID, "icons/action_add_traces.png"));
  }

  @Override
  public void run() {
    AddTraces.addTraces(view);
  }
}