package zephyr.plugin.core.commands;



public class SuspendAll extends ControlCommand {
  public SuspendAll() {
    super("zephyr.plugin.core.commands.suspend", false);
  }

  @Override
  protected void controlAction() {
    control.suspend();
  }
}
