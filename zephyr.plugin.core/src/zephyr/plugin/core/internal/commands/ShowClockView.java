package zephyr.plugin.core.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import zephyr.plugin.core.internal.views.ClocksView;

public class ShowClockView extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage();
    try {
      activePage.showView(ClocksView.ViewID);
    } catch (PartInitException e) {
      e.printStackTrace();
    }
    return null;
  }
}
