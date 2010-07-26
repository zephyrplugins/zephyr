package zephyr.plugin.core.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import rlpark.plugin.utils.time.Clock;
import rlpark.plugin.utils.time.ClockKillable;
import zephyr.plugin.core.ZephyrPluginCommon;
import zephyr.plugin.core.views.SyncView;

public class KillClock extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage();
    IWorkbenchPart activePart = activePage.getActivePart();
    if (!(activePart instanceof SyncView))
      return null;
    Clock clock = ZephyrPluginCommon.viewBinder().findClock((SyncView) activePart);
    if (clock == null)
      return null;
    if (clock instanceof ClockKillable)
      ((ClockKillable) clock).kill();
    return null;
  }
}
