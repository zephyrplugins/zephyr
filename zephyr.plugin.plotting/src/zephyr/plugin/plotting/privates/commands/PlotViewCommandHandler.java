package zephyr.plugin.plotting.privates.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import zephyr.plugin.plotting.privates.view.PlotView;

public abstract class PlotViewCommandHandler extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage();
    IWorkbenchPart activePart = activePage.getActivePart();
    PlotView plotView = (PlotView) activePart;
    execute(plotView);
    return null;
  }

  abstract protected void execute(PlotView plotView);
}
