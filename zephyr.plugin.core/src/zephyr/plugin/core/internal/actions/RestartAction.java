package zephyr.plugin.core.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.internal.utils.Helper;
import zephyr.plugin.core.internal.views.Restartable;

public class RestartAction extends Action {
  private final Restartable view;

  public RestartAction(Restartable view) {
    super("Restart", IAction.AS_PUSH_BUTTON);
    this.view = view;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrCore.PluginID, "icons/action_restart.gif"));
  }

  @Override
  public void run() {
    view.restart();
  }
}
