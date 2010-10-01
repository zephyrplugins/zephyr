package zephyr.plugin.critterview.runnable;

import critterbot.actions.CritterbotAction;
import critterbot.environment.CritterbotEnvironments;
import critterbot.samples.ConstantAgent;

public class SimulatorRunnable extends RobotRunnable {
  public SimulatorRunnable() {
    super(CritterbotEnvironments.createSimulatorEnvironment(), new ConstantAgent(CritterbotAction.DoNothing));
  }
}