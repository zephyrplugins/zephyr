package zephyr.plugin.core.privates.commands;


public class ResumeAll extends ControlCommand {
  public ResumeAll() {
    super("zephyr.plugin.core.commands.resume");
  }

  @Override
  protected void controlAction() {
    control.resume();
  }

  @Override
  public boolean isEnabled() {
    return control.hasOneClockSuspended();
  }
}
