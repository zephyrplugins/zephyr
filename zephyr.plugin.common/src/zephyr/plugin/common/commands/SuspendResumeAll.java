package zephyr.plugin.common.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import zephyr.plugin.common.ZephyrPluginCommon;
import zephyr.plugin.common.control.Control;

public class SuspendResumeAll extends AbstractHandler {
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Control control = ZephyrPluginCommon.control();
    if (control.isSuspended())
      control.resume();
    else
      control.suspend();
    return null;
  }
}
