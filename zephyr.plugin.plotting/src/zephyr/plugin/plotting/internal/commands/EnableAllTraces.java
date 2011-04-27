package zephyr.plugin.plotting.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;

public class EnableAllTraces extends AbstractHandler {
  public static final String ID = "zephyr.plugin.plotting.commands.enablealltraces";

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Command command = event.getCommand();
    boolean oldValue = HandlerUtil.toggleCommandState(command);
    ZephyrPluginPlotting.tracesManager().setForceEnabled(!oldValue);
    return null;
  }

}
