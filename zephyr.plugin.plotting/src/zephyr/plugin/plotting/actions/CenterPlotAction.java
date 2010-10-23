package zephyr.plugin.plotting.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import zephyr.plugin.core.utils.Helper;
import zephyr.plugin.plotting.ZephyrPluginPlotting;

public class CenterPlotAction extends Action {
  public interface ViewCenterable {
    void center();
  }

  private final ViewCenterable view;

  public CenterPlotAction(ViewCenterable view) {
    super("Center", IAction.AS_PUSH_BUTTON);
    this.view = view;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrPluginPlotting.PLUGIN_ID, "icons/action_centerplot.png"));
  }

  @Override
  public void run() {
    view.center();
  }
}
