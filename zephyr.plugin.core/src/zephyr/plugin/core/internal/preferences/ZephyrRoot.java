package zephyr.plugin.core.internal.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import zephyr.plugin.core.internal.ZephyrPluginCore;

public class ZephyrRoot
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  public ZephyrRoot() {
    super(GRID);
    setPreferenceStore(ZephyrPluginCore.getDefault().getPreferenceStore());
    setDescription("");
  }

  @Override
  public void createFieldEditors() {
  }

  @Override
  public void init(IWorkbench workbench) {
  }
}