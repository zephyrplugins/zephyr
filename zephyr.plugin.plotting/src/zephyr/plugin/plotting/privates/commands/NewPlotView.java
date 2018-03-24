package zephyr.plugin.plotting.privates.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import zephyr.plugin.plotting.privates.view.PlotView;

public class NewPlotView extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage();
    IViewReference referenceView = null;
    String secondaryID = null;
    int secondaryIndex = 0;
    while (secondaryID == null || referenceView != null) {
      secondaryIndex++;
      secondaryID = String.valueOf(secondaryIndex);
      referenceView = activePage.findViewReference(PlotView.ID, secondaryID);
    }
    try {
      activePage.showView(PlotView.ID, secondaryID, IWorkbenchPage.VIEW_ACTIVATE);
    } catch (PartInitException e) {
      throw new ExecutionException(e.getMessage(), e);
    }
    return null;
  }
}
