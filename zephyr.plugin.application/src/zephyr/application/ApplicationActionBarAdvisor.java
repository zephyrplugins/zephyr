package zephyr.application;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
  final private Separator mainMenuSeparator = new Separator("zephyr.mainmenu.end");
  private IWorkbenchAction aboutAction;
  private IWorkbenchAction preferencesAction;

  public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
    super(configurer);
    mainMenuSeparator.setVisible(false);
  }

  @Override
  protected void makeActions(IWorkbenchWindow window) {
    aboutAction = ActionFactory.ABOUT.create(window);
    register(aboutAction);
    preferencesAction = ActionFactory.PREFERENCES.create(window);
    register(preferencesAction);
  }

  @Override
  protected void fillMenuBar(IMenuManager menuBar) {
    menuBar.add(mainMenuSeparator);

    MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
    menuBar.add(windowMenu);
    ActionContributionItem preferencesItem = new ActionContributionItem(preferencesAction);
    preferencesItem.setVisible(!isMac());
    windowMenu.add(preferencesItem);

    MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
    menuBar.add(helpMenu);
    ActionContributionItem aboutItem = new ActionContributionItem(aboutAction);
    aboutItem.setVisible(!isMac());
    helpMenu.add(aboutItem);

    helpMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    helpMenu.add(new Separator());
  }

  private static boolean isMac() {
    String platform = SWT.getPlatform();
    return platform.equals("carbon") || platform.equals("cocoa");
  }
}
