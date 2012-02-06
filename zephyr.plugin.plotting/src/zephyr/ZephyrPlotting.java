package zephyr;

import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;
import zephyr.plugin.plotting.preferences.PreferenceConstants;

public class ZephyrPlotting {
  public static final String PluginID = "zephyr.plugin.plotting";

  static public int preferredLineWidth() {
    return ZephyrPluginPlotting.getDefault().getPreferenceStore().getInt(PreferenceConstants.LineSizePrefLabel);
  }

  static public boolean preferredAntiAliasing() {
    return ZephyrPluginPlotting.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.AntiAliasingPrefLabel);
  }
}
