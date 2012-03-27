package zephyr.plugin.junittesting.support.checklisteners;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.views.ViewWithControl;

public class ControlChecks {

  static Control findControl(String viewID) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    IWorkbenchPage page = window.getActivePage();
    ViewWithControl part = null;
    try {
      part = (ViewWithControl) page.showView(viewID);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    Control control = part.control();
    return control;
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
