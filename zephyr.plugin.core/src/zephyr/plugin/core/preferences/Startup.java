package zephyr.plugin.core.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import zephyr.plugin.core.ZephyrPluginCommon;

public class Startup
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  public Startup() {
    super(GRID);
    setPreferenceStore(ZephyrPluginCommon.getDefault().getPreferenceStore());
    setDescription("Zephyr startup preference page");
  }

  @Override
  public void createFieldEditors() {
    addField(new StringFieldEditor(PreferenceKeys.StartupCommandLineKey,
                                   "&Startup arguments:", getFieldEditorParent()));
  }

  @Override
  public void init(IWorkbench workbench) {
  }
}