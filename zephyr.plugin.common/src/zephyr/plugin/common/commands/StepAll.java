package zephyr.plugin.common.commands;


public class StepAll extends ControlCommand {
  public StepAll() {
    super("zephyr.plugin.common.commands.step", true);
  }

  @Override
  protected void controlAction() {
    control.step();
  }
}
