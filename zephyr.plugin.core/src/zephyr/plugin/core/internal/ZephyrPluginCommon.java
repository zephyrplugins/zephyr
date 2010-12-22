package zephyr.plugin.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import zephyr.ZephyrCore;
import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.advertisement.Advertisement;
import zephyr.plugin.core.api.advertisement.Advertisement.Advertised;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.control.Control;
import zephyr.plugin.core.internal.preferences.PreferenceKeys;
import zephyr.plugin.core.internal.synchronization.ViewBinder;
import zephyr.plugin.core.internal.synchronization.tasks.ViewTaskScheduler;

public class ZephyrPluginCommon extends AbstractUIPlugin {
  public Signal<Runnable> onRunnableStarted = new Signal<Runnable>();
  public static final String PLUGIN_ID = "zephyr.plugin.core";
  private static boolean shuttingDown = false;
  private static boolean synchronous;

  final ViewBinder viewBinder = new ViewBinder();
  final private ViewTaskScheduler viewTaskScheduler = new ViewTaskScheduler();
  private static ZephyrPluginCommon plugin;
  private final ThreadGroup threadGroup = new ThreadGroup("ZephyrRunnable");

  private final Control control = new Control();

  static public List<String> getArgsFiltered() {
    String[] args = Platform.getCommandLineArgs();
    List<String> result = new ArrayList<String>();
    for (String arg : args)
      if (!arg.startsWith("-"))
        result.add(arg);
    String startupPreferences = getDefault().getPreferenceStore().getString(PreferenceKeys.StartupCommandLineKey);
    for (String arg : startupPreferences.split(" "))
      if (!arg.isEmpty())
        result.add(arg);
    return result;
  }

  public void startZephyrMain(final RunnableFactory runnableFactory) {
    Thread runnableThread = new Thread(threadGroup, new Runnable() {
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
    });
    runnableThread.setName("ZephyrRunnable-" + threadGroup.activeCount());
    runnableThread.setDaemon(true);
    runnableThread.start();
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
    Zephyr.advertisement().onAdvertise.connect(new Listener<Advertisement.Advertised>() {
      @Override
      public void listen(Advertised eventInfo) {
        ZephyrCore.advertise(eventInfo.clock, eventInfo.advertised, eventInfo.info);
      }
    });
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

  static public ViewTaskScheduler viewScheduler() {
    return getDefault().viewTaskScheduler;
  }

  public static ZephyrPluginCommon getDefault() {
    return plugin;
  }

  @SuppressWarnings("unchecked")
  public Class<? extends Object> loadClass(String className) throws ClassNotFoundException {
    return plugin.getBundle().loadClass(className);
  }

  public static void setSynchronous(boolean value) {
    synchronous = value;
  }

  public static boolean synchronous() {
    return synchronous;
  }

  public static boolean isShuttingDown() {
    return shuttingDown;
  }

  public static void shutDown() {
    shuttingDown = true;
  }
}