package zephyr.application;


import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.misc.Policy;


/**
 * This class controls all aspects of the application's execution
 */
@SuppressWarnings("restriction")
public class Application implements IApplication {

  @Override
  public Object start(IApplicationContext context) {
    Display.setAppName("Zephyr");
    // I guess this line should not exist
    Policy.DEBUG_SWT_GRAPHICS = ZephyrApplication.getDebugOption("/trace/graphics");
    Display display = PlatformUI.createDisplay();
    if (ZephyrApplication.getDebugOption("/trace/graphics")) {
      Sleak sleak = new Sleak();
      sleak.open();
    }
    try {
      int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
      if (returnCode == PlatformUI.RETURN_RESTART)
        return IApplication.EXIT_RESTART;
      return IApplication.EXIT_OK;
    } finally {
      display.dispose();
    }
  }

  @Override
  public void stop() {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    if (workbench == null)
      return;
    final Display display = workbench.getDisplay();
    display.syncExec(new Runnable() {
      @Override
      public void run() {
        if (!display.isDisposed())
          workbench.close();
      }
    });
  }
}
