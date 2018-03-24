package zephyr.plugin.protobuf;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ZephyrPluginProtobuf extends AbstractUIPlugin {
  private static ZephyrPluginProtobuf plugin;

  public ZephyrPluginProtobuf() {
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

  public static ZephyrPluginProtobuf getDefault() {
    return plugin;
  }
}
