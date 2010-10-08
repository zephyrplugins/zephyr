package zephyr;

import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.ZephyrPluginCommon;
import zephyr.plugin.core.api.synchronization.Clock;

public class ZephyrCore {
  static public void advertize(Clock clock, Object drawn) {
    ZephyrPluginCommon.viewBinder().bindViews(clock, drawn, null);
  }

  static public void advertize(Clock clock, Object drawn, Object info) {
    ZephyrPluginCommon.viewBinder().bindViews(clock, drawn, info);
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
