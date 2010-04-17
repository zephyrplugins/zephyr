package zephyr.application;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
  private IWorkbenchAction aboutAction;

  public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
    super(configurer);
  }

  @Override
  protected void makeActions(IWorkbenchWindow window) {
    aboutAction = ActionFactory.ABOUT.create(window);
    register(aboutAction);
  }

  @Override
  protected void fillMenuBar(IMenuManager menuBar) {
    MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
    menuBar.add(helpMenu);
    ActionContributionItem item = new ActionContributionItem(aboutAction);
    item.setVisible(!isMac());
    helpMenu.add(item);
  }

  private boolean isMac() {
    String platform = SWT.getPlatform();
    return platform.equals("carbon") || platform.equals("cocoa");
  }
}
