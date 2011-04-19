package zephyr.plugin.core.internal;

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
import zephyr.plugin.core.SyncCode;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.Zephyr.AdvertisementInfo;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.control.Control;
import zephyr.plugin.core.internal.preferences.PreferenceKeys;
import zephyr.plugin.core.internal.synchronization.ViewBinder;
import zephyr.plugin.core.internal.synchronization.tasks.ViewTaskScheduler;
import zephyr.plugin.core.internal.views.PopupViewTraverser;
import zephyr.plugin.core.views.SyncView;

public class ZephyrPluginCore extends AbstractUIPlugin {
  static private boolean zephyrEnabled = false;
  private static boolean synchronous;

  final ViewBinder viewBinder = new ViewBinder();
  final SyncCode syncCode = new SyncCode();
  final private ViewTaskScheduler viewTaskScheduler = new ViewTaskScheduler();
  private static ZephyrPluginCore plugin;
  private final ThreadGroup threadGroup = new ThreadGroup("ZephyrRunnable");
  private final ZephyrClassLoaderInternal classLoader;
  private final Control control = new Control();

  public ZephyrPluginCore() {
    classLoader = AccessController.doPrivileged(new PrivilegedAction<ZephyrClassLoaderInternal>() {
      @Override
      public ZephyrClassLoaderInternal run() {
        return new ZephyrClassLoaderInternal();
      }
    });
    Zephyr.onAdvertised.connect(new Listener<AdvertisementInfo>() {
      @Override
      public void listen(AdvertisementInfo eventInfo) {
        Clock clock = eventInfo.clock;
        ClassNode rootNode = syncCode.parse(clock, eventInfo.advertised);
        CodeTrees.traverse(new PopupViewTraverser(clock), rootNode);
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

  public static ZephyrPluginCore getDefault() {
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
    // enableActivities("zephyr.plugin.core.activity");
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
}