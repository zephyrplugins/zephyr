package zephyr.plugin.critterview;

import java.io.File;

import zephyr.plugin.common.utils.Helper;
import critterbot.environment.CritterbotSimulator;

public class StartupJob implements zephyr.plugin.common.startup.StartupJob {
  @Override
  public int level() {
    return 0;
  }

  @Override
  public void run() {
    String path = Helper.getPluginLocation(CritterviewPlugin.getDefault(),
                                           "./libs/CritterbotSimulator.jar");
    CritterbotSimulator.setJarPath(new File(path).getAbsolutePath());
  }
}
