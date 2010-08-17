package zephyr;

import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.ZephyrPluginCommon;
import zephyr.plugin.core.api.synchronization.Clock;

public class Zephyr {
  static public void advertize(Clock clock, Object drawn) {
    ZephyrPluginCommon.viewBinder().bindViews(clock, "", drawn);
  }

  static public void advertize(Clock clock, String info, Object drawn) {
    ZephyrPluginCommon.viewBinder().bindViews(clock, info, drawn);
  }

  public static void start(RunnableFactory runnableFactory) {
    ZephyrPluginCommon.getDefault().startZephyrMain(runnableFactory);
  }

  public static void start(final Runnable runnable) {
    ZephyrPluginCommon.getDefault().startZephyrMain(new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        return runnable;
      }
    });
  }
}
