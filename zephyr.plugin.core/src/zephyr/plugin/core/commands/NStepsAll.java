package zephyr.plugin.core.commands;

import org.eclipse.swt.widgets.Display;

import zephyr.plugin.core.dialog.NbStepsDialog;


public class NStepsAll extends ControlCommand {
  public NStepsAll() {
    super("zephyr.plugin.core.commands.nsteps", true);
  }

  @Override
  protected void controlAction() {
    NbStepsDialog dialog = new NbStepsDialog(Display.getDefault().getActiveShell());
    int nbTimeSteps = dialog.getNumberOfTimeSteps();
    if (nbTimeSteps > 0)
      control.step(nbTimeSteps);
  }
}
