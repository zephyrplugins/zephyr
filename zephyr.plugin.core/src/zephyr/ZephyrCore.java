package zephyr;

import java.util.List;

import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCommon;

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

  public static void removeClock(Clock clock) {
    ZephyrPluginCommon.viewBinder().removeClock(clock);
  }

  public static List<String> getArgsFiltered() {
    return ZephyrPluginCommon.getArgsFiltered();
  }

  public static Class<? extends Object> loadClass(String className) throws ClassNotFoundException {
    return ZephyrPluginCommon.getDefault().loadClass(className);
  }
}
