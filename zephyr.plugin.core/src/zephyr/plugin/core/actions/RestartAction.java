package zephyr.plugin.core.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import zephyr.ZephyrCore;
import zephyr.plugin.core.utils.Helper;
import zephyr.plugin.core.views.Restartable;

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