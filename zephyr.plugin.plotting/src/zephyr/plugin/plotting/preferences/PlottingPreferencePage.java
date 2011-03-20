package zephyr.plugin.plotting.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;

public class PlottingPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  public PlottingPreferencePage() {
    super(GRID);
    setPreferenceStore(ZephyrPluginPlotting.getDefault().getPreferenceStore());
    setDescription("Zephyr Plotting Preferences");
  }

  @Override
  public void createFieldEditors() {
    addField(new BooleanFieldEditor(PreferenceConstants.AntiAliasingPrefLabel, "&Antializing", getFieldEditorParent()));
    final IntegerFieldEditor lineSizeField = new IntegerFieldEditor(PreferenceConstants.LineSizePrefLabel,
                                                                    "&Line size:", getFieldEditorParent());
    lineSizeField.setValidRange(1, 10);
    addField(lineSizeField);
  }

  @Override
  public void init(IWorkbench workbench) {
  }
}