package zephyr.plugin.core.internal;

import zephyr.plugin.core.ZephyrPluginCommon;
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
    ZephyrPluginCommon.synchronous = Helper.booleanState(SYNCHRONOUS, false);
    ZephyrPluginCommon.control().connectSuspendOnJobStarting();
  }
}
