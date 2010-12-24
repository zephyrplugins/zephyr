package zephyr.plugin.core.internal;

import org.eclipse.ui.IStartup;

import zephyr.ZephyrCore;

public class Startup implements IStartup {

  @Override
  public void earlyStartup() {
    if (!ZephyrCore.zephyrEnabled())
      ZephyrPluginCore.enableActivities("zephyr.ui.editors.activity");
  }
}
