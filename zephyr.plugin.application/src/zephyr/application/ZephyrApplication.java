package zephyr.application;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ZephyrApplication extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "zephyr.application";

  private static ZephyrApplication plugin;

  public ZephyrApplication() {
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public static ZephyrApplication getDefault() {
    return plugin;
  }

  public Shell shell() {
    return plugin.getWorkbench().getActiveWorkbenchWindow().getShell();
  }

  public static boolean getDebugOption(String option) {
    return "true".equalsIgnoreCase(Platform.getDebugOption(PlatformUI.PLUGIN_ID + option));
  }
}
