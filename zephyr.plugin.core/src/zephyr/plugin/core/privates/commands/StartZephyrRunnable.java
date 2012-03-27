package zephyr.plugin.core.privates.commands;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IConfigurationElement;

import zephyr.plugin.core.ZephyrCore;

public class StartZephyrRunnable extends AbstractHandler {
  private static final String ParameterRunnableID = "zephyr.plugin.core.command.parameter.runnableid";


  @SuppressWarnings("rawtypes")
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Map parameters = event.getParameters();
    String runnableID = (String) parameters.get(ParameterRunnableID);
    if (runnableID == null)
      return null;
    IConfigurationElement configurationElement = ZephyrCore.findRunnable(runnableID);
    if (configurationElement == null) {
      System.err.println("Zephyr error: " + runnableID + " runnable id not found");
      return null;
    }
    ZephyrCore.start(configurationElement);
    return null;
  }
}
