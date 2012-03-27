package zephyr.plugin.core.privates.treeview;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;

public class SelectionTreeListener implements SelectionListener {
  @Override
  public void widgetSelected(SelectionEvent event) {
    TreeItem treeItem = (TreeItem) event.item;
    if (event.item == null)
      return;
    CodeNode codeNode = (CodeNode) treeItem.getData();
    if (codeNode != null)
      ZephyrCore.sendStatusBarMessage(buildDescriptiveLabel(codeNode));
  }

  static protected String buildDescriptiveLabel(CodeNode codeNode) {
    StringBuilder label = new StringBuilder(CodeTrees.mergePath(codeNode.path()));
    String nodeInfo = CodeTrees.nodeInfo(codeNode);
    if (!nodeInfo.isEmpty()) {
      label.append(": ");
      label.append(nodeInfo);
    }
    return label.toString();
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent event) {
  }
}