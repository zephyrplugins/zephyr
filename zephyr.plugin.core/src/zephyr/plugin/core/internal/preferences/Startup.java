package zephyr.plugin.core.internal.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import zephyr.plugin.core.internal.ZephyrPluginCore;

public class Startup
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  public Startup() {
    super(GRID);
    setPreferenceStore(ZephyrPluginCore.getDefault().getPreferenceStore());
    setDescription("Zephyr startup preference page");
  }

  @Override
  public void createFieldEditors() {
    addField(new FileFieldEditor(PreferenceKeys.StartupCommandLineKey,
                                 "&Startup arguments:", getFieldEditorParent()));
  }

  @Override
  public void init(IWorkbench workbench) {
  }
}