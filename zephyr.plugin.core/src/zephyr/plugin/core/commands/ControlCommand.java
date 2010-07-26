package zephyr.plugin.core.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import zephyr.plugin.core.ZephyrPluginCommon;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.control.Control;
import zephyr.plugin.core.utils.Helper;

public abstract class ControlCommand extends AbstractHandler implements Listener<Control> {
  protected final Control control = ZephyrPluginCommon.control();
  protected final Command command;
  private final boolean actionWhenSuspended;

  public ControlCommand(String commandID, boolean actionWhenSuspended) {
    this.actionWhenSuspended = actionWhenSuspended;
    command = Helper.getCommand(commandID);
    control.onModeChange.connect(this);
  }

  abstract protected void controlAction();

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    controlAction();
    return null;
  }

  @Override
  public void listen(Control control) {
    command.setHandler(isEnabled() ? this : null);
  }

  @Override
  public boolean isEnabled() {
    final boolean suspended = ZephyrPluginCommon.control().isSuspended();
    return actionWhenSuspended ? suspended : !suspended;
  }
}
