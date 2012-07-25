package zephyr.plugin.core.privates;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.Zephyr.AdvertisementInfo;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.SyncCode;
import zephyr.plugin.core.internal.async.BusEvent;
import zephyr.plugin.core.internal.async.recognizers.OnEventBlocker;
import zephyr.plugin.core.internal.events.AdvertizeEvent;
import zephyr.plugin.core.internal.views.SyncView;
import zephyr.plugin.core.privates.async.ZephyrBusEvent;
import zephyr.plugin.core.privates.clocks.Clocks;
import zephyr.plugin.core.privates.clocks.Control;
import zephyr.plugin.core.privates.preferences.PreferenceKeys;
import zephyr.plugin.core.privates.synchronization.binding.ViewBinder;
import zephyr.plugin.core.privates.synchronization.tasks.ViewTaskScheduler;

public class ZephyrPluginCore extends AbstractUIPlugin {
  static private boolean zephyrEnabled = false;

  final ViewBinder viewBinder = new ViewBinder();
  final SyncCode syncCode = new SyncCode();
  final private ViewTaskScheduler viewTaskScheduler = new ViewTaskScheduler();
  private static ZephyrPluginCore plugin;
  private final ThreadGroup threadGroup = new ThreadGroup("ZephyrRunnable");
  private final ZephyrClassLoaderInternal classLoader;
  private final Control control = new Control();
  final ZephyrBusEvent busEvent = new ZephyrBusEvent();
  private final Clocks clocks = new Clocks();

  public ZephyrPluginCore() {
    Clock.setEnableDataLock(true);
    classLoader = AccessController.doPrivileged(new PrivilegedAction<ZephyrClassLoaderInternal>() {
      @Override
      public ZephyrClassLoaderInternal run() {
        return new ZephyrClassLoaderInternal();
      }
    });
    Zephyr.onAdvertised.connect(new Listener<AdvertisementInfo>() {
      @Override
      public void listen(AdvertisementInfo eventInfo) {
        RecognizeZephyrInitialization recognizer = new RecognizeZephyrInitialization(eventInfo);
        if (recognizer.isSatisfied())
          return;
        OnEventBlocker codeParsedBlocker = busEvent.createWaiter(recognizer);
        codeParsedBlocker.connect();
        busEvent.dispatch(new AdvertizeEvent(eventInfo));
        codeParsedBlocker.block();
      }
    });
  }

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
    busEvent.registerRecorders();
    busEvent.start();
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  static public BusEvent busEvent() {
    return getDefault().busEvent;
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

  public static ZephyrPluginCore getDefault() {
    return plugin;
  }

  public Class<? extends Object> loadClass(String className) throws ClassNotFoundException {
    return plugin.getBundle().loadClass(className);
  }

  public static void setupPartListener() {
    PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
      @Override
      public void windowOpened(IWorkbenchWindow window) {
        for (IWorkbenchPage page : window.getPages())
          pageOpened(page);
        window.addPageListener(new IPageListener() {
          @Override
          public void pageOpened(IWorkbenchPage page) {
            ZephyrPluginCore.pageOpened(page);
          }

          @Override
          public void pageClosed(IWorkbenchPage page) {
          }

          @Override
          public void pageActivated(IWorkbenchPage page) {
          }
        });
      }

      @Override
      public void windowDeactivated(IWorkbenchWindow window) {
      }

      @Override
      public void windowClosed(IWorkbenchWindow window) {
      }

      @Override
      public void windowActivated(IWorkbenchWindow window) {
      }
    });
  }

  static protected void pageOpened(IWorkbenchPage page) {
    page.addPartListener(new IPartListener() {
      @Override
      public void partOpened(IWorkbenchPart part) {
      }

      @Override
      public void partDeactivated(IWorkbenchPart part) {
      }

      @Override
      public void partClosed(IWorkbenchPart part) {
        if (part instanceof SyncView)
          ZephyrPluginCore.viewBinder().disposeView((SyncView) part);
      }

      @Override
      public void partBroughtToTop(IWorkbenchPart part) {
      }

      @Override
      public void partActivated(IWorkbenchPart part) {
      }
    });
  }

  static public void enableZephyrActivity() {
    zephyrEnabled = true;
    enableActivities("zephyr.plugin.core.activity");
  }

  static void enableActivities(String... ids) {
    final IWorkbenchActivitySupport activitySupport = PlatformUI.getWorkbench().getActivitySupport();
    IActivityManager activityManager = activitySupport.getActivityManager();
    Set<String> enabledActivities = new HashSet<String>();
    for (String id : ids)
      if (activityManager.getActivity(id).isDefined())
        enabledActivities.add(id);
    final Set<String> definedActivities = enabledActivities;
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        activitySupport.setEnabledActivityIds(definedActivities);
      }
    });
  }

  static public SyncCode syncCode() {
    return plugin.syncCode;
  }

  public static boolean isZephyrEnabled() {
    return zephyrEnabled;
  }

  public ClassLoader classLoader() {
    return classLoader;
  }

  public static Clocks clocks() {
    return plugin.clocks;
  }
}