package zephyr.plugin.jython.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import zephyr.plugin.core.api.internal.codeparser.CodeParsers;

public class JythonPlugin extends AbstractUIPlugin {
  static private JythonPlugin plugin;

  public JythonPlugin() {
    super();
  }

  @Override
  public void start(BundleContext context) throws Exception {
    plugin = this;
    super.start(context);
    CodeParsers.registerParser(new ObjectPyListParser());
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public static JythonPlugin getDefault() {
    return plugin;
  }
}
