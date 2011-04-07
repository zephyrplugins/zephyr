package zephyr.plugin.core.internal.commands;


public class StepAll extends ControlCommand {
  public StepAll() {
    super("zephyr.plugin.core.commands.step");
  }

  @Override
  protected void controlAction() {
    control.step();
  }

  @Override
  public boolean isEnabled() {
    return control.hasOneClockSuspended();
  }
}
