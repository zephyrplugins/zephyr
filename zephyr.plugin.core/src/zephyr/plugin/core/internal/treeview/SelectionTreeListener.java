package zephyr.plugin.core.internal.treeview;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;

public class SelectionTreeListener implements SelectionListener {
  @Override
  public void widgetSelected(SelectionEvent event) {
    TreeItem treeItem = (TreeItem) event.item;
    CodeNode codeNode = (CodeNode) treeItem.getData();
    if (codeNode != null)
      ZephyrCore.sendStatusBarMessage(codeNode.label());
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent event) {
  }
}