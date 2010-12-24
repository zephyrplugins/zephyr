package zephyr.plugin.core.internal.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import zephyr.plugin.core.internal.ZephyrPluginCore;

public class Synchronous extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Command command = event.getCommand();
    ZephyrPluginCore.setSynchronous(!HandlerUtil.toggleCommandState(command));
    return null;
  }
}
