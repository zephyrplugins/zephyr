package zephyr.plugin.core.dialog;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import zephyr.ZephyrCore;

public class NbStepsDialog extends InputDialog {

  private final static IInputValidator validator = new IInputValidator() {
    @Override
    public String isValid(String newText) {
      try {
        value = Integer.parseInt(newText);
      } catch (NumberFormatException e) {
        return "Not a integer";
      }
      if (value <= 0)
        return "Not a strictly positive number of time steps";
      return null;
    }
  };
  static int value = -1;

  private static String TimeStepsKey = "defaultNbTimeSteps";
  private final static IEclipsePreferences instanceScope = new InstanceScope().getNode(ZephyrCore.PluginID);

  public NbStepsDialog(Shell parentShell) {
    super(parentShell, "N Steps", "Number of time steps", getTimeStepValue(), validator);
    setBlockOnOpen(true);
  }

  private static String getTimeStepValue() {
    return instanceScope.get(TimeStepsKey, "2");
  }

  public int getNumberOfTimeSteps() {
    setBlockOnOpen(true);
    int dialogResult = open();
    if (dialogResult != Window.OK)
      return -1;
    instanceScope.put(TimeStepsKey, String.valueOf(value));
    return value;
  }
}
