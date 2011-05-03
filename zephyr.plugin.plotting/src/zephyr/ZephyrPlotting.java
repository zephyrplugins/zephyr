package zephyr;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;
import zephyr.plugin.plotting.preferences.PreferenceConstants;

public class ZephyrPlotting {
  public static final String PluginID = "zephyr.plugin.plotting";

  static public int preferredLineSize() {
    return ZephyrPluginPlotting.getDefault().getPreferenceStore().getInt(PreferenceConstants.LineSizePrefLabel);
  }

  static public boolean preferredAntiAliasing() {
    return ZephyrPluginPlotting.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.AntiAliasingPrefLabel);
  }

  static public DataMonitor createMonitor(Clock clock) {
    return ZephyrPluginPlotting.tracesManager().dataMonitor(clock);
  }
}
