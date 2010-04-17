package zephyr.plugin.common.commands;


public class SuspendAll extends ControlCommand {
  public SuspendAll() {
    super("zephyr.plugin.common.commands.suspend", false);
  }

  @Override
  protected void controlAction() {
    control.suspend();
  }
}
