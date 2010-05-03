package zephyr.plugin.console;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ZephyrConsolePlugin extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "zephyr.plugin.console";

  private static ZephyrConsolePlugin plugin;
  private final MessageConsole systemConsole = new MessageConsole("System", null);

  public ZephyrConsolePlugin() {
    ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { systemConsole });
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

  public MessageConsole systemConsole() {
    return systemConsole;
  }

  public static ZephyrConsolePlugin getDefault() {
    return plugin;
  }
}
