package zephyr;

import java.util.Collection;
import java.util.List;

import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.startup.StartupJobs;

public class ZephyrCore {
  public static final String PluginID = "zephyr.plugin.core";

  static public void advertiseInstance(Clock clock, Object drawn, Object info) {
    ZephyrPluginCore.viewBinder().bindViews(clock, drawn, info);
  }

  public static void start(RunnableFactory runnableFactory) {
    ZephyrPluginCore.getDefault().startZephyrMain(runnableFactory);
  }

  public static void start(final Runnable runnable) {
    ZephyrPluginCore.getDefault().startZephyrMain(new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        return runnable;
      }
    });
  }

  public static void removeClock(Clock clock) {
    ZephyrPluginCore.viewBinder().removeClock(clock);
  }

  public static List<String> getArgsFiltered() {
    return ZephyrPluginCore.getArgsFiltered();
  }

  public static ClassLoader classLoader() {
    return ZephyrPluginCore.getDefault().classLoader();
  }

  static public boolean zephyrEnabled() {
    return ZephyrPluginCore.isZephyrEnabled();
  }

  public static void start() {
    ZephyrPluginCore.setupPartListener();
    ZephyrPluginCore.enableZephyrActivity();
    new StartupJobs().schedule();
  }

  public static void shutDown() {
    ZephyrPluginCore.viewScheduler().dispose();
    Collection<Clock> clocks = ZephyrSync.getClocks();
    for (Clock clock : clocks)
      clock.terminate();
    for (Clock clock : clocks)
      ZephyrPluginCore.viewBinder().removeClock(clock);
  }
}
