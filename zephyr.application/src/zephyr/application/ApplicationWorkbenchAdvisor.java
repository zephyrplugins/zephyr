package zephyr.application;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import zephyr.ZephyrCore;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

  @Override
  public void initialize(IWorkbenchConfigurer configurer) {
    super.initialize(configurer);
    configurer.setSaveAndRestore(true);
    ZephyrCore.start();
  }

  @Override
  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
    return new ApplicationWorkbenchWindowAdvisor(configurer);
  }

  @Override
  public String getInitialWindowPerspectiveId() {
    return "zephyr.plugin.core.perspective.initial";
  }

  @Override
  public void preStartup() {
  }

  @Override
  public boolean preShutdown() {
    ZephyrCore.shutDown();
    return true;
  }
}
