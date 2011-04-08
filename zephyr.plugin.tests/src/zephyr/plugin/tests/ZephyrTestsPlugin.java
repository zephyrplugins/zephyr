package zephyr.plugin.tests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import zephyr.plugin.tests.codeparser.CodeParser;


public class ZephyrTestsPlugin extends AbstractUIPlugin {
  static private ZephyrTestsPlugin plugin;
  private final CodeParser codeParser;

  public ZephyrTestsPlugin() {
    codeParser = new CodeParser();
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

  static public CodeParser codeParser() {
    return plugin.codeParser;
  }
}
