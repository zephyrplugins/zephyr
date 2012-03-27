package zephyr.plugin.core.privates.commands;

import org.eclipse.swt.widgets.Display;

import zephyr.plugin.core.internal.dialog.NbStepsDialog;


public class NStepsAll extends ControlCommand {
  public NStepsAll() {
    super("zephyr.plugin.core.commands.nsteps");
  }

  @Override
  protected void controlAction() {
    NbStepsDialog dialog = new NbStepsDialog(Display.getDefault().getActiveShell());
    int nbTimeSteps = dialog.getNumberOfTimeSteps();
    if (nbTimeSteps > 0)
      control.step(nbTimeSteps);
  }

  @Override
  public boolean isEnabled() {
    return control.hasOneClockSuspended();
  }
}
