package zephyr.plugin.core.privates.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import zephyr.plugin.core.privates.ZephyrPluginCore;

public class ZephyrRoot extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public ZephyrRoot() {
    super(GRID);
    setPreferenceStore(ZephyrPluginCore.getDefault().getPreferenceStore());
    setDescription("Zephyr Preferences");
  }

  @Override
  public void createFieldEditors() {
    addField(new StringFieldEditor(PreferenceKeys.StartupCommandLineKey, "&Startup arguments", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceKeys.OpenClockViewKey, "&Pop up clock view", getFieldEditorParent()));
  }

  @Override
  public void init(IWorkbench workbench) {
  }
}