package zephyr.plugin.core.internal.treeview;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;

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
    StringBuilder label = new StringBuilder(codeNode.path());
    String nodeInfo = CodeTrees.nodeInfo(codeNode);
    if (!nodeInfo.isEmpty()) {
      label.append("[");
      label.append(nodeInfo);
      label.append("]");
    }
    return label.toString();
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent event) {
  }
}