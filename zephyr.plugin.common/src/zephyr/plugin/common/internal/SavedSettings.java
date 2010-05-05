package zephyr.plugin.common.internal;

import zephyr.plugin.common.ZephyrPluginCommon;
import zephyr.plugin.common.startup.StartupJob;
import zephyr.plugin.common.utils.Helper;

public class SavedSettings implements StartupJob {
  public static final String STARTSUSPENDED = "zephyr.plugin.common.commands.startsuspended";
  private static final String SYNCHRONOUS = "zephyr.plugin.common.commands.synchronous";

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
