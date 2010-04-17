package zephyr.plugin.critterview.commands;

import java.io.IOException;
import java.net.UnknownHostException;

import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotSimulator;

public class StartSimulator extends RunEnvironmentCommand {
  @Override
  protected CritterbotEnvironment createEnvironment() throws UnknownHostException, IOException {
    return new CritterbotSimulator(true);
  }
}
