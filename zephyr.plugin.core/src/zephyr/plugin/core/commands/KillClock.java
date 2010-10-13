package zephyr.plugin.core.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import zephyr.plugin.core.api.synchronization.Closeable;

public class KillClock extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage();
    IWorkbenchPart activePart = activePage.getActivePart();
    if (!(activePart instanceof Closeable))
      return null;
    ((Closeable) activePart).close();
    return null;
  }
}
