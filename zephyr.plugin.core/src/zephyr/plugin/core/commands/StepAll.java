package zephyr.plugin.core.commands;


public class StepAll extends ControlCommand {
  public StepAll() {
    super("zephyr.plugin.core.commands.step", true);
  }

  @Override
  protected void controlAction() {
    control.step();
  }
}
