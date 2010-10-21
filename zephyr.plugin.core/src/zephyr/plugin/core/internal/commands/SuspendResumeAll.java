package zephyr.plugin.core.internal.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import zephyr.plugin.core.control.Control;
import zephyr.plugin.core.internal.ZephyrPluginCommon;

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
