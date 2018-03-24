package zephyr.plugin.core.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.api.synchronization.Closeable;
import zephyr.plugin.core.internal.utils.Helper;

public class TerminateAction extends Action {
  private final Closeable view;

  public TerminateAction(Closeable view) {
    super("Terminate", IAction.AS_PUSH_BUTTON);
    this.view = view;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrCore.PluginID, "icons/action_terminate.gif"));
  }

  @Override
  public void run() {
    view.close();
  }
}
