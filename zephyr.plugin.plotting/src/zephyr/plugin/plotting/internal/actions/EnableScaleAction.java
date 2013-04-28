package zephyr.plugin.plotting.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IMemento;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.internal.utils.Helper;

public final class EnableScaleAction extends Action {
  private static final String CenterActionKey = "CenterAction";
  private boolean centerData = false;

  public EnableScaleAction() {
    super("Center", IAction.AS_CHECK_BOX);
    setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID, "icons/action_centerplot.png"));
    setId("zephyr.plugin.plotting.action.togglecenter");
    setChecked(centerData);
  }

  @Override
  public void run() {
    centerData = !centerData;
  }

  public boolean scaleEnabled() {
    return centerData;
  }

  public void init(IMemento memento) {
    Boolean saved = memento != null ? memento.getBoolean(CenterActionKey) : null;
    if (saved != null) {
      saved = false;
      centerData = saved;
      setChecked(saved);
    }
  }

  public void saveState(IMemento memento) {
    memento.putBoolean(CenterActionKey, isChecked());
  }
}