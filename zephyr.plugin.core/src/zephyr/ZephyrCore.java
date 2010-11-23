package zephyr;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;

import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCommon;

public class ZephyrCore {
  static private boolean zephyrEnabled = false;

  static public void advertise(Clock clock, Object drawn) {
    ZephyrPluginCommon.viewBinder().bindViews(clock, drawn, null);
  }

  static public void advertise(Clock clock, Object drawn, Object info) {
    ZephyrPluginCommon.viewBinder().bindViews(clock, drawn, info);
  }

  public static void start(RunnableFactory runnableFactory) {
    ZephyrPluginCommon.getDefault().startZephyrMain(runnableFactory);
  }

  public static void start(final Runnable runnable) {
    ZephyrPluginCommon.getDefault().startZephyrMain(new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        return runnable;
      }
    });
  }

  public static void removeClock(Clock clock) {
    ZephyrPluginCommon.viewBinder().removeClock(clock);
  }

  public static List<String> getArgsFiltered() {
    return ZephyrPluginCommon.getArgsFiltered();
  }

  public static Class<? extends Object> loadClass(String className) throws ClassNotFoundException {
    return ZephyrPluginCommon.getDefault().loadClass(className);
  }

  static public void enableZephyr() {
    ZephyrCore.enableActivities("zephyr.plugin.core.activity");
    zephyrEnabled = true;
  }

  static public boolean zephyrEnabled() {
    return zephyrEnabled;
  }

  public static void enableActivities(String... ids) {
    IWorkbenchActivitySupport activitySupport = PlatformUI.getWorkbench().getActivitySupport();
    IActivityManager activityManager = activitySupport.getActivityManager();
    Set<String> enabledActivities = new HashSet<String>();
    for (String id : ids)
      if (activityManager.getActivity(id).isDefined())
        enabledActivities.add(id);
    Set<String> definedActivities = enabledActivities;
    activitySupport.setEnabledActivityIds(definedActivities);
  }
}
