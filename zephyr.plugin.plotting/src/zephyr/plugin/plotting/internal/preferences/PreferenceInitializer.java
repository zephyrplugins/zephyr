package zephyr.plugin.plotting.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import zephyr.plugin.plotting.privates.ZephyrPluginPlotting;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = ZephyrPluginPlotting.getDefault().getPreferenceStore();
    store.setDefault(PreferenceConstants.AntiAliasingPrefLabel, false);
    store.setDefault(PreferenceConstants.LineSizePrefLabel, 1);
  }
}
