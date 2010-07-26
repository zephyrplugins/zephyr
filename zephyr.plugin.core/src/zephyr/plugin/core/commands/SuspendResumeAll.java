package zephyr.plugin.core.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import zephyr.plugin.core.ZephyrPluginCommon;
import zephyr.plugin.core.control.Control;

public class SuspendResumeAll extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Control control = ZephyrPluginCommon.control();
    if (control.isSuspended())
      control.resume();
    else
      control.suspend();
    return null;
  }
}
