package zephyr;

import java.util.Collection;

import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.views.SyncView;

public class ZephyrSync {
  public static void bind(Clock clock, SyncView view) {
    ZephyrPluginCore.viewBinder().bind(clock, view);
  }

  public static void unbind(Clock clock, SyncView view) {
    ZephyrPluginCore.viewBinder().unbind(clock, view);
  }

  public static Signal<Runnable> onRunnableStarted() {
    return ZephyrPluginCore.getDefault().onRunnableStarted;
  }


  public static void declareClock(Clock clock) {
    ZephyrPluginCore.viewBinder().addClock(clock);
  }

  public static Signal<Clock> onClockAdded() {
    return ZephyrPluginCore.viewBinder().onClockAdded;
  }

  public static Signal<Clock> onClockRemoved() {
    return ZephyrPluginCore.viewBinder().onClockRemoved;
  }

  public static void submitView(SyncView view) {
    ZephyrPluginCore.viewScheduler().submitView(view);
  }

  public static Collection<Clock> getClocks() {
    return ZephyrPluginCore.viewBinder().getClocks();
  }

  public static void suspend(Clock clock) {
    ZephyrPluginCore.control().suspend(clock);
  }

}
