package zephyr.plugin.plotting.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import zephyr.plugin.plotting.ZephyrPluginPlotting;

public class EnableAllTraces extends AbstractHandler {
  public static String ID = "zephyr.plugin.plotting.commands.enablealltraces";

  public Object execute(ExecutionEvent event) throws ExecutionException {
    Command command = event.getCommand();
    boolean oldValue = HandlerUtil.toggleCommandState(command);
    ZephyrPluginPlotting.clockTracesManager().setForceEnabled(!oldValue);
    return null;
  }

}
