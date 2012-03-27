package zephyr.plugin.core.privates.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public class StartSuspended extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Command command = event.getCommand();
    HandlerUtil.toggleCommandState(command);
    return null;
  }
}
