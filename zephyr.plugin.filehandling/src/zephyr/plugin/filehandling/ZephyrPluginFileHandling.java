package zephyr.plugin.filehandling;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ZephyrPluginFileHandling extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "zephyr.plugin.filehandling";
  private static ZephyrPluginFileHandling plugin;

  private final FileLoader fileLoader = new FileLoader();

  public ZephyrPluginFileHandling() {
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

  public static ZephyrPluginFileHandling getDefault() {
    return plugin;
  }

  public static FileLoader fileLoader() {
    return getDefault().fileLoader;
  }
}
