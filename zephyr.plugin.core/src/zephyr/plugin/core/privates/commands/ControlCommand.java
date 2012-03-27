package zephyr.plugin.core.privates.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.internal.utils.Helper;
import zephyr.plugin.core.privates.ZephyrPluginCore;
import zephyr.plugin.core.privates.clocks.Control;

public abstract class ControlCommand extends AbstractHandler implements Listener<Control> {
  protected final Control control = ZephyrPluginCore.control();
  protected final Command command;

  public ControlCommand(String commandID) {
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
  public abstract boolean isEnabled();
}
