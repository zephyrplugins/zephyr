package zephyr.plugin.tests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import zephyr.plugin.tests.codeparser.CodeTrees;


public class ZephyrTestsPlugin extends AbstractUIPlugin {
  public static final String PluginID = "zephyr.plugin.tests";
  static private ZephyrTestsPlugin plugin;
  private final CodeTrees codeParser;

  public ZephyrTestsPlugin() {
    codeParser = new CodeTrees();
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

  static public CodeTrees codeParser() {
    return plugin.codeParser;
  }
}
