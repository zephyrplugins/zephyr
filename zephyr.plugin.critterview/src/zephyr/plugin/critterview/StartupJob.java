package zephyr.plugin.critterview;

import java.io.File;

import zephyr.plugin.core.utils.Helper;
import critterbot.environment.CritterbotSimulator;

public class StartupJob implements zephyr.plugin.core.startup.StartupJob {
  @Override
  public int level() {
    return 100;
  }

  @Override
  public void run() {
    String path = Helper.getPluginLocation(CritterviewPlugin.getDefault(),
                                           "./libs/CritterbotSimulator.jar");
    CritterbotSimulator.setJarPath(new File(path).getAbsolutePath());
  }
}
