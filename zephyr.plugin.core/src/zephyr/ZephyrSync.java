package zephyr;

import java.util.List;

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

  public static void submitView(SyncView view) {
    ZephyrPluginCore.viewScheduler().submitView(view);
  }

  public static void suspend(Clock clock) {
    ZephyrPluginCore.control().suspend(clock);
  }

  public static List<Clock> getClocks() {
    return ZephyrPluginCore.clocks().getClocks();
  }
}
