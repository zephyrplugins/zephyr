package zephyr.plugin.junittesting.support.checklisteners;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.views.ViewWithControl;

public class ControlChecks {
  public static IViewPart showView(String viewID) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    if (window == null)
      return null;
    IWorkbenchPage page = window.getActivePage();
    try {
      return page.showView(viewID);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Control findControl(String viewID) {
    return ((ViewWithControl) showView(viewID)).control();
  }

  public static int countChildren(String viewID) {
    CheckEvent event = new CheckEvent(CheckEvent.CountChildrenID, viewID);
    ZephyrSync.busEvent().dispatch(event);
    return event.pullResult();
  }

  public static int countColors(String viewID) {
    CheckEvent event = new CheckEvent(CheckEvent.CountColorID, viewID);
    ZephyrSync.busEvent().dispatch(event);
    return event.pullResult();
  }
}
