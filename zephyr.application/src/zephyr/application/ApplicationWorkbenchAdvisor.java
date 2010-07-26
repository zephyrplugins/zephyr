package zephyr.application;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import zephyr.plugin.core.Perspective;
import zephyr.plugin.core.ZephyrPluginCommon;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

  @Override
  public void initialize(IWorkbenchConfigurer configurer) {
    super.initialize(configurer);
    configurer.setSaveAndRestore(true);
  }

  @Override
  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
      IWorkbenchWindowConfigurer configurer) {
    return new ApplicationWorkbenchWindowAdvisor(configurer);
  }

  @Override
  public String getInitialWindowPerspectiveId() {
    return Perspective.PERSPECTIVE_ID;
  }

  @Override
  public void postStartup() {
    IWorkbenchActivitySupport activitySupport = PlatformUI.getWorkbench().getActivitySupport();
    IActivityManager activityManager = activitySupport.getActivityManager();
    Set<String> enabledActivities = new HashSet<String>();
    String id = "zephyr.plugin.core.activity";
    if (activityManager.getActivity(id).isDefined())
      enabledActivities.add(id);
    activitySupport.setEnabledActivityIds(enabledActivities);
  }

  @Override
  public boolean preShutdown() {
    ZephyrPluginCommon.shuttingDown = true;
    return true;
  }

  @Override
  public void postShutdown() {
    if (!ZephyrPluginCommon.viewBinder().isEmpty())
      System.err.println("*** Post shutdown: viewBinder is not empty. Some views are still binded");
  }
}
