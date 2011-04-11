package zephyr.plugin.core.internal.commands;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import zephyr.plugin.core.internal.StartZephyrMain;

public class StartZephyrRunnable extends AbstractHandler {
  private static final String ParameterRunnableID = "zephyr.plugin.core.command.parameter.runnableid";


  @SuppressWarnings("rawtypes")
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Map parameters = event.getParameters();
    String runnableID = (String) parameters.get(ParameterRunnableID);
    if (runnableID == null)
      return null;
    IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("zephyr.runnable");
    IConfigurationElement configurationElement = null;
    for (IExtension extension : extensionPoint.getExtensions())
      for (IConfigurationElement element : extension.getConfigurationElements())
        if (runnableID.equals(element.getAttribute("id"))) {
          configurationElement = element;
          break;
        }
    if (configurationElement == null) {
      System.err.println("Zephyr error: " + runnableID + " runnable id not found");
      return null;
    }
    StartZephyrMain.start(configurationElement);
    return null;
  }
}
