package zephyr.plugin.console.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.handlers.HandlerUtil;

public class ShowConsole extends AbstractHandler {
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage();
    try {
      activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW);
    } catch (PartInitException e) {
      e.printStackTrace();
    }
    return null;
  }
}
