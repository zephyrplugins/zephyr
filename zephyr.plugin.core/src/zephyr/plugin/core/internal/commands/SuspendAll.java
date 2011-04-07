package zephyr.plugin.core.internal.commands;


public class SuspendAll extends ControlCommand {
  public SuspendAll() {
    super("zephyr.plugin.core.commands.suspend");
  }

  @Override
  protected void controlAction() {
    control.suspend();
  }

  @Override
  public boolean isEnabled() {
    return control.hasOneClockRunning();
  }
}
