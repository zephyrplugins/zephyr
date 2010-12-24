package zephyr.plugin.core.internal;

import zephyr.plugin.core.startup.StartupJob;
import zephyr.plugin.core.utils.Helper;

public class SavedSettings implements StartupJob {
  public static final String STARTSUSPENDED = "zephyr.plugin.core.commands.startsuspended";
  private static final String SYNCHRONOUS = "zephyr.plugin.core.commands.synchronous";

  @Override
  public int level() {
    return 10;
  }

  @Override
  public void run() {
    ZephyrPluginCore.setSynchronous(Helper.booleanState(SYNCHRONOUS, false));
    ZephyrPluginCore.control().connectSuspendOnJobStarting();
  }
}
