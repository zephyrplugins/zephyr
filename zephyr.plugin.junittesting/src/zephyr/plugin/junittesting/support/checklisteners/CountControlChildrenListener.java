package zephyr.plugin.junittesting.support.checklisteners;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.UIListener;
import zephyr.plugin.core.views.ViewWithControl;

public class CountControlChildrenListener extends UIListener {
  @Override
  protected void listenInUIThread(Event event) {
    CheckEvent checkEvent = (CheckEvent) event;
    Control control = findControl(checkEvent.viewID());
    checkEvent.setResult(countChildrenRecursive(control));
  }

  private Control findControl(String viewID) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    IWorkbenchPage page = window.getActivePage();
    ViewWithControl part = null;
    try {
      part = (ViewWithControl) page.showView(viewID);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    Control control = part.control();
    return control;
  }

  private int countChildrenRecursive(Control control) {
    if (control == null)
      return -1;
    if (control.isDisposed())
      return 0;
    if (control instanceof Tree)
      return countTreeSize((Tree) control);
    if (!(control instanceof Composite))
      return 0;
    Composite composite = (Composite) control;
    int nbChildren = composite.getChildren().length;
    for (Control child : composite.getChildren())
      nbChildren += countChildrenRecursive(child);
    return nbChildren;
  }

  private int countTreeSize(Tree tree) {
    int nbChildren = tree.getItems().length;
    for (TreeItem treeItem : tree.getItems())
      nbChildren += countTreeSizeRecursive(treeItem);
    return nbChildren;
  }

  private int countTreeSizeRecursive(TreeItem item) {
    int nbChildren = item.getItemCount();
    for (TreeItem treeItem : item.getItems())
      nbChildren += countTreeSizeRecursive(treeItem);
    return nbChildren;
  }
}
