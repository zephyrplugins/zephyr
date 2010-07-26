package zephyr.plugin.critterview.runnable;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.logging.abstracts.Logger;
import critterbot.CritterbotAgent;
import critterbot.actions.CritterbotAction;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotEnvironments;
import critterbot.samples.ConstantAgent;

public class RobotRunnable implements Runnable {
  private final CritterbotEnvironment environment;
  private final CritterbotAgent agent;

  public RobotRunnable() {
    this(CritterbotEnvironments.createRobotEnvironment(), new ConstantAgent(CritterbotAction.DoNothing));
  }

  public RobotRunnable(CritterbotEnvironment environment, CritterbotAgent agent) {
    this.environment = environment;
    this.agent = agent;
  }

  @Override
  public void run() {
    if (environment == null || agent == null)
      return;
    Logger logger = ZephyrPlotting.createLogger(Labels.label(environment), environment.clock());
    logger.add(environment);
    environment.run(agent);
    environment.close();
  }
}