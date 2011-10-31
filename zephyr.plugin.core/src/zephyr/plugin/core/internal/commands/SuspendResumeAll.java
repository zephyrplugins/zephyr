package zephyr.plugin.core.internal.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.clocks.Control;

public class SuspendResumeAll extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Control control = ZephyrPluginCore.control();
    if (control.hasOneClockRunning())
      control.suspend();
    else
      control.resume();
    return null;
  }
}
