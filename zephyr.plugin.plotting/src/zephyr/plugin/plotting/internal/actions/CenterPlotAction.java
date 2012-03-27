package zephyr.plugin.plotting.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.internal.utils.Helper;

public class CenterPlotAction extends Action {
  public interface ViewCenterable {
    void center();
  }

  private final ViewCenterable view;

  public CenterPlotAction(ViewCenterable view) {
    super("Center", IAction.AS_PUSH_BUTTON);
    this.view = view;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID, "icons/action_centerplot.png"));
  }

  @Override
  public void run() {
    view.center();
  }
}
