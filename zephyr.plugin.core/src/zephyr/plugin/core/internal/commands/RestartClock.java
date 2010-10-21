package zephyr.plugin.core.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import zephyr.plugin.core.views.Restartable;

public class RestartClock extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage();
    IWorkbenchPart activePart = activePage.getActivePart();
    if (!(activePart instanceof Restartable))
      return null;
    ((Restartable) activePart).restart();
    return null;
  }
}
