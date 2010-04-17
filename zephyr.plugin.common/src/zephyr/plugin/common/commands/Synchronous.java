package zephyr.plugin.common.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import zephyr.plugin.common.ZephyrPluginCommon;

public class Synchronous extends AbstractHandler {
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Command command = event.getCommand();
    ZephyrPluginCommon.synchronous = !HandlerUtil.toggleCommandState(command);
    return null;
  }
}
