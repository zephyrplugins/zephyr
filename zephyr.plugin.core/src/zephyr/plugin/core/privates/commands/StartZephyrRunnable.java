package zephyr.plugin.core.privates.commands;

import java.util.Map;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import zephyr.plugin.core.RunnableFactory;
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
    RunnableFactory runnableFactory = ZephyrCore.findRunnable(runnableID);
    if (runnableFactory == null) {
      System.err.println("Zephyr error: " + runnableID + " runnable id not found");
      return null;
    }
    ZephyrCore.start(runnableFactory);
    return null;
  }
}
