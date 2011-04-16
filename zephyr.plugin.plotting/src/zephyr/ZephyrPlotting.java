package zephyr;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;
import zephyr.plugin.plotting.internal.traces.ClockTracesManager;
import zephyr.plugin.plotting.preferences.PreferenceConstants;

public class ZephyrPlotting {
  public static final String PluginID = "zephyr.plugin.plotting";

  static public DataMonitor createMonitor(Clock clock) {
    final ClockTracesManager manager = ClockTracesManager.manager();
    if (manager == null)
      return null;
    return manager.addClock(clock.info().label(), clock);
  }

  static public int preferredLineSize() {
    return ZephyrPluginPlotting.getDefault().getPreferenceStore().getInt(PreferenceConstants.LineSizePrefLabel);
  }

  static public boolean preferredAntiAliasing() {
    return ZephyrPluginPlotting.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.AntiAliasingPrefLabel);
  }

  /**
   * Use Zephyr.advertize
   */
  @Deprecated
  static public DataMonitor createLogger(String label, Clock clock) {
    return ClockTracesManager.manager().addClock(label, clock);
  }
}
