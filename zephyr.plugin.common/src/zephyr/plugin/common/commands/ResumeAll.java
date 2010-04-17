package zephyr.plugin.common.commands;


public class ResumeAll extends ControlCommand {
  public ResumeAll() {
    super("zephyr.plugin.common.commands.resume", true);
  }

  @Override
  protected void controlAction() {
    control.resume();
  }
}
