package zephyr.plugin.plotting.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.advertisement.Advertisement;
import zephyr.plugin.core.api.advertisement.Advertisement.Advertised;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.utils.Helper;
import zephyr.plugin.plotting.internal.commands.EnableAllTraces;
import zephyr.plugin.plotting.internal.traces.ClockTracesManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class ZephyrPluginPlotting extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "zephyr.plugin.plotting";
  private static ZephyrPluginPlotting plugin;
  private final ClockTracesManager traces = new ClockTracesManager();
  private final Listener<Advertisement.Advertised> advertisedRootListener = new Listener<Advertisement.Advertised>() {
    @Override
    public void listen(Advertised eventInfo) {
      DataMonitor logger = ZephyrPlotting.createMonitor(eventInfo.clock);
      logger.add(eventInfo.advertised);
    }
  };

  public ZephyrPluginPlotting() {
    traces.setForceEnabled(Helper.booleanState(EnableAllTraces.ID, false));
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
    ClockTracesManager.setManager(traces);
    Zephyr.advertisement().onAdvertiseRoot.connect(advertisedRootListener);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    Zephyr.advertisement().onAdvertiseRoot.disconnect(advertisedRootListener);
    plugin = null;
    ClockTracesManager.setManager(null);
    super.stop(context);
  }

  public static ZephyrPluginPlotting getDefault() {
    return plugin;
  }
}
