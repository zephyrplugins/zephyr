package zephyr.plugin.critterview.commands;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import zephyr.Zephyr;
import zephyr.plugin.critterview.runnable.RobotRunnable;
import critterbot.actions.CritterbotAction;
import critterbot.environment.CritterbotEnvironment;
import critterbot.samples.ConstantAgent;

public abstract class RunEnvironmentCommand extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    try {
      CritterbotEnvironment environment = createEnvironment();
      if (environment == null)
        return null;
      Zephyr.start(new RobotRunnable(environment, createAgent()));
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  protected ConstantAgent createAgent() {
    return new ConstantAgent(CritterbotAction.DoNothing);
  }

  abstract protected CritterbotEnvironment createEnvironment() throws UnknownHostException, IOException;
}
