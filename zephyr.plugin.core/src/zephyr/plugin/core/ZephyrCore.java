package zephyr.plugin.core;

import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.events.AtomicEvent;
import zephyr.plugin.core.internal.events.Events;
import zephyr.plugin.core.internal.events.StartRunnableEvent;
import zephyr.plugin.core.privates.StartZephyrMain;
import zephyr.plugin.core.privates.ZephyrPluginCore;
import zephyr.plugin.core.privates.startup.StartupJobs;
import zephyr.plugin.core.privates.synchronization.binding.ClockViews;
import zephyr.plugin.core.privates.synchronization.binding.SynchronizationMode.Mode;

public class ZephyrCore {
  public static final String PluginID = "zephyr.plugin.core";

  public static void start(RunnableFactory runnableFactory) {
    ZephyrPluginCore.getDefault().startZephyrMain(runnableFactory);
  }

  public static void start(final Runnable runnable) {
    ZephyrPluginCore.getDefault().startZephyrMain(new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        return runnable;
      }
    });
  }

  public static void removeClock(Clock clock) {
    ZephyrPluginCore.clocks().remove(clock);
  }

  public static List<String> getArgsFiltered() {
    return ZephyrPluginCore.getArgsFiltered();
  }

  public static ClassLoader classLoader() {
    return ZephyrPluginCore.getDefault().classLoader();
  }

  static public boolean zephyrEnabled() {
    return ZephyrPluginCore.isZephyrEnabled();
  }

  public static void start() {
    ZephyrPluginCore.setupPartListener();
    ZephyrPluginCore.enableZephyrActivity();
    ZephyrSync.busEvent().dispatch(new AtomicEvent(Events.ZephyrStartingEvent));
    new StartupJobs().schedule();
  }

  public static void shutDown() {
    ZephyrPluginCore.viewScheduler().dispose();
    Collection<Clock> clocks = ZephyrSync.getClocks();
    for (Clock clock : clocks)
      clock.prepareTermination();
    for (Clock clock : clocks)
      ZephyrPluginCore.viewBinder().removeClock(clock);
  }

  public static void setSynchronous(boolean value) {
    ClockViews.synchronizationMode.setMode(value ? Mode.Synchrone : Mode.Asynchrone, -1);
  }

  public static void sendStatusBarMessage(final String message) {
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        setStatusMessage(message);
      }
    });
  }

  static public void setStatusMessage(String message) {
    IWorkbench wb = PlatformUI.getWorkbench();
    IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
    if (window == null) {
      return;
    }
    IWorkbenchPage page = window.getActivePage();
    if (page == null) {
      return;
    }
    IWorkbenchPart part = page.getActivePart();
    if (part == null) {
      return;
    }
    IWorkbenchPartSite site = part.getSite();
    IViewSite vSite = (IViewSite) site;
    IActionBars actionBars = vSite.getActionBars();
    if (actionBars == null) {
      return;
    }
    IStatusLineManager statusLineManager = actionBars.getStatusLineManager();
    if (statusLineManager == null) {
      return;
    }
    statusLineManager.setMessage(message.replace("\n", " "));
  }

  static public RunnableFactory findRunnable(String runnableID, String[] parameters) {
    IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("zephyr.runnable");
    IConfigurationElement configurationElement = null;
    for (IExtension extension : extensionPoint.getExtensions())
      for (IConfigurationElement element : extension.getConfigurationElements())
        if (runnableID.equals(element.getAttribute("id"))) {
          configurationElement = element;
          break;
        }
    if (configurationElement == null) {
      return null;
    }
    return StartZephyrMain.createRunnableFactory(configurationElement, parameters);
  }

  public static List<Clock> registeredClocks() {
    return ZephyrPluginCore.clocks().getClocks();
  }

  public static boolean restartClock(Clock clock) {
    Thread thread = Thread.currentThread();
    RunnableFactory factory = ZephyrPluginCore.threadStarter().factory(thread);
    if (factory == null) {
      return false;
    }
    ZephyrSync.busEvent().dispatch(new StartRunnableEvent(factory));
    return true;
  }
}
