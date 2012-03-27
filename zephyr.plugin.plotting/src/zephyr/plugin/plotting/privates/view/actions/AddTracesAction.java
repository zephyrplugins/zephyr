package zephyr.plugin.plotting.privates.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.internal.utils.Helper;
import zephyr.plugin.plotting.privates.commands.AddTraces;
import zephyr.plugin.plotting.privates.view.PlotView;

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
