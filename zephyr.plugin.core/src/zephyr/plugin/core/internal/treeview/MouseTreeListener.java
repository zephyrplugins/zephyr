package zephyr.plugin.core.internal.treeview;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.internal.synchronization.providers.ViewProviderReference;

public class MouseTreeListener implements MouseListener {
  class PopupItemListener extends SelectionAdapter {
    private final String viewID;

    public PopupItemListener(String viewID) {
      this.viewID = viewID;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
      treeItemSelected(viewID);
    }
  }

  final ViewAssociator viewAssociator;
  private final Menu menu;
  private final Tree tree;
  private final List<MenuItem> popupItems = new ArrayList<MenuItem>();

  public MouseTreeListener(Tree tree, ViewAssociator viewAssociator) {
    this.tree = tree;
    this.viewAssociator = viewAssociator;
    menu = new Menu(tree.getShell(), SWT.POP_UP);
  }

  void treeItemSelected(String viewID) {
    CodeNode[] selection = StructureExplorerView.getSelection(tree);
    viewAssociator.showSelection(selection, viewID);
  }

  @Override
  public void mouseDoubleClick(MouseEvent event) {
    treeItemSelected(null);
  }

  @Override
  public void mouseDown(MouseEvent event) {
    if (event.button != 3)
      return;
    CodeNode[] selection = StructureExplorerView.getSelection(tree);
    Set<ViewProviderReference> providers = viewAssociator.buildProviders(selection);
    disposePreviousItems();
    if (providers.size() > 0)
      buildPopupMenu(selection, providers);
    else
      buildNoViewAvailableMenu();
    menu.setVisible(true);
  }

  private void buildNoViewAvailableMenu() {
    MenuItem item = new MenuItem(menu, SWT.PUSH);
    item.setText("<No View Available>");
    item.setEnabled(false);
    popupItems.add(item);
  }

  private void disposePreviousItems() {
    for (MenuItem menuItem : popupItems)
      menuItem.dispose();
    popupItems.clear();
  }

  private void buildPopupMenu(CodeNode[] selection, Set<ViewProviderReference> providers) {
    for (ViewProviderReference reference : viewAssociator.buildProviders(selection)) {
      MenuItem item = new MenuItem(menu, SWT.PUSH);
      item.setText("Show in " + reference.name());
      item.addSelectionListener(new PopupItemListener(reference.viewID()));
      popupItems.add(item);
    }
  }

  @Override
  public void mouseUp(MouseEvent event) {
  }

  public void dispose() {
    menu.dispose();
  }
}
