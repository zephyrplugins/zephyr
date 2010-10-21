package zephyr.plugin.core.internal.commands;



public class ResumeAll extends ControlCommand {
  public ResumeAll() {
    super("zephyr.plugin.core.commands.resume", true);
  }

  @Override
  protected void controlAction() {
    control.resume();
  }
}
