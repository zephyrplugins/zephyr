package zephyr.plugin.common.internal;

import zephyr.plugin.common.ZephyrPluginCommon;
import zephyr.plugin.common.startup.StartupJob;
import zephyr.plugin.common.utils.Helper;

public class SavedSettings implements StartupJob {
  private static final String SYNCHRONOUS = "zephyr.plugin.common.commands.synchronous";
  private static final String STARTSUSPENDED = "zephyr.plugin.common.commands.startsuspended";

  @Override
  public int level() {
    return 10;
  }

  @Override
  public void run() {
    ZephyrPluginCommon.synchronous = Helper.booleanState(SYNCHRONOUS, false);
    if (Helper.booleanState(STARTSUSPENDED, false))
      ZephyrPluginCommon.control().connectSuspendOnJobStarting();
  }

}
