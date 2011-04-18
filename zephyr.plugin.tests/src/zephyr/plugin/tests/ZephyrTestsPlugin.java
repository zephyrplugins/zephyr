package zephyr.plugin.tests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ZephyrTestsPlugin extends AbstractUIPlugin {
  public static final String PluginID = "zephyr.plugin.tests";
  static private ZephyrTestsPlugin plugin;

  public ZephyrTestsPlugin() {
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

  static public ZephyrTestsPlugin plugin() {
    return plugin;
  }
}
