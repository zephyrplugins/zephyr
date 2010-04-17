package zephyr;

import rlpark.plugin.utils.time.Clock;
import zephyr.plugin.common.RunnableFactory;
import zephyr.plugin.common.ZephyrPluginCommon;

public class Zephyr {
  static public void advertize(Clock clock, Object drawn) {
    ZephyrPluginCommon.viewBinder().bindViews(clock, drawn);
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
