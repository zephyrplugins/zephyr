package zephyr.plugin.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import rlpark.plugin.utils.events.Signal;
import zephyr.plugin.core.control.Control;
import zephyr.plugin.core.views.ViewBinder;

public class ZephyrPluginCommon extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "zephyr.plugin.core";
  public static boolean shuttingDown = false;
  public static boolean synchronous;
  public static Signal<Runnable> onRunnableStarted = new Signal<Runnable>();

  private static ZephyrPluginCommon plugin;

  private final ViewBinder viewBinder = new ViewBinder();
  private final Control control = new Control();

  static public List<String> getArgsFiltered() {
    String[] args = Platform.getCommandLineArgs();
    List<String> result = new ArrayList<String>();
    for (String arg : args)
      if (!arg.startsWith("-"))
        result.add(arg);
    return result;
  }

  public void startZephyrMain(final RunnableFactory runnableFactory) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        Runnable runnable = runnableFactory.createRunnable();
        if (runnable == null)
          return;
        onRunnableStarted.fire(runnable);
        try {
          runnable.run();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
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

  static public ViewBinder viewBinder() {
    return getDefault().viewBinder;
  }

  static public Control control() {
    return getDefault().control;
  }

  public static ZephyrPluginCommon getDefault() {
    return plugin;
  }

  @SuppressWarnings("unchecked")
  public Class<? extends Object> loadClass(String className) throws ClassNotFoundException {
    return plugin.getBundle().loadClass(className);
  }
}
