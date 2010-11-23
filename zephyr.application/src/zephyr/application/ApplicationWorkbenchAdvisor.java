package zephyr.application;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import zephyr.ZephyrCore;
import zephyr.ZephyrSync;

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
    return "zephyr.plugin.core.perspective.initial";
  }

  @Override
  public void postStartup() {
    ZephyrCore.enableZephyr();
  }

  @Override
  public boolean preShutdown() {
    ZephyrSync.shutDown();
    return true;
  }

  @Override
  public void postShutdown() {
    if (!ZephyrSync.isSyncEmpty())
      System.err.println("*** Post shutdown: viewBinder is not empty. Some views are still binded.");
  }
}
