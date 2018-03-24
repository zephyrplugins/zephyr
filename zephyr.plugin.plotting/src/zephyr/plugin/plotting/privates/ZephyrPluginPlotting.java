package zephyr.plugin.plotting.privates;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitorRegistry;
import zephyr.plugin.plotting.privates.commands.EnableAllTraces;
import zephyr.plugin.plotting.privates.traces.ClockTracesManager;

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
    @SuppressWarnings("cast")
    ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
    Command command = commandService.getCommand(EnableAllTraces.ID);
    State state = command.getState(RegistryToggleState.STATE_ID);
    traces.setForceEnabled((Boolean) state.getValue());
    MonitorRegistry.registerFactory(traces);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    MonitorRegistry.unregisterFactory(traces);
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
