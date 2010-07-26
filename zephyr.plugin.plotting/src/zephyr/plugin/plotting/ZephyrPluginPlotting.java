package zephyr.plugin.plotting;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import zephyr.plugin.core.utils.Helper;
import zephyr.plugin.plotting.commands.EnableAllTraces;
import zephyr.plugin.plotting.traces.ClockTracesManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class ZephyrPluginPlotting extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "zephyr.plugin.plotting";
  private static ZephyrPluginPlotting plugin;

  private final ClockTracesManager traces = new ClockTracesManager();

  public ZephyrPluginPlotting() {
    traces.setForceEnabled(Helper.booleanState(EnableAllTraces.ID, false));
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

  public static ZephyrPluginPlotting getDefault() {
    return plugin;
  }


  static public ClockTracesManager clockTracesManager() {
    return getDefault().traces;
  }
}
