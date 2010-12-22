package zephyr;

import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCommon;
import zephyr.plugin.core.internal.startup.StartupJobs;
import zephyr.plugin.core.internal.synchronization.ClockViews;
import zephyr.plugin.core.views.SyncView;

public class ZephyrSync {
  public static void start() {
    new StartupJobs().schedule();
  }

  public static void shutDown() {
    ZephyrPluginCommon.shutDown();
  }

  public static boolean isSyncEmpty() {
    return ZephyrPluginCommon.viewBinder().isEmpty();
  }

  public static void bind(Clock clock, SyncView view) {
    ZephyrPluginCommon.viewBinder().bind(clock, view);
  }

  public static void unbind(Clock clock, SyncView view) {
    ZephyrPluginCommon.viewBinder().unbind(clock, view);
  }

  public static Signal<Runnable> onRunnableStarted() {
    return ZephyrPluginCommon.getDefault().onRunnableStarted;
  }

  public static Signal<Clock> onClockRemoved() {
    return ZephyrPluginCommon.viewBinder().onClockRemoved;
  }

  public static void disposeView(SyncView view) {
    ZephyrPluginCommon.viewBinder().disposeView(view);
  }

  public static void submitView(SyncView view) {
    ClockViews.submitView(view);
  }
}
