package zephyr;

import java.util.Collection;
import java.util.List;

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

import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.SyncCode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.startup.StartupJobs;

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
    ZephyrPluginCore.viewBinder().removeClock(clock);
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
    new StartupJobs().schedule();
  }

  public static void shutDown() {
    ZephyrPluginCore.viewScheduler().dispose();
    Collection<Clock> clocks = ZephyrSync.getClocks();
    for (Clock clock : clocks)
      clock.terminate();
    for (Clock clock : clocks)
      ZephyrPluginCore.viewBinder().removeClock(clock);
  }

  public static void sendStatusBarMessage(final String message) {
    Display.getCurrent().syncExec(new Runnable() {
      @Override
      public void run() {
        IWorkbench wb = PlatformUI.getWorkbench();
        IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        IWorkbenchPart part = page.getActivePart();
        IWorkbenchPartSite site = part.getSite();
        IViewSite vSite = (IViewSite) site;
        IActionBars actionBars = vSite.getActionBars();
        if (actionBars == null)
          return;
        IStatusLineManager statusLineManager = actionBars.getStatusLineManager();
        if (statusLineManager == null)
          return;
        statusLineManager.setMessage(message);
      }
    });
  }

  public static SyncCode syncCode() {
    return ZephyrPluginCore.syncCode();
  }
}
