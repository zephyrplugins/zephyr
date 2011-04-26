package zephyr.plugin.plotting.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import zephyr.plugin.plotting.internal.traces.ClockTracesManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class ZephyrPluginPlotting extends AbstractUIPlugin {
  private static ZephyrPluginPlotting plugin;
  private final ClockTracesManager traces = new ClockTracesManager();

  public ZephyrPluginPlotting() {
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

  static public ClockTracesManager tracesManager() {
    return getDefault().traces;
  }
}
