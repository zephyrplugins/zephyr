package zephyr.plugin.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import zephyr.plugin.core.internal.ZephyrPluginCore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = ZephyrPluginCore.getDefault().getPreferenceStore();
    store.setDefault(PreferenceKeys.StartupCommandLineKey, "");
    store.setDefault(PreferenceKeys.OpenClockViewKey, true);
  }

}
