package zephyr.plugin.core.internal.treeview;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
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

  protected String buildDescriptiveLabel(CodeNode codeNode) {
    StringBuilder label = new StringBuilder(codeNode.path());
    if (codeNode instanceof ClassNode) {
      String className = ((ClassNode) codeNode).instance().getClass().getSimpleName();
      appendSuffix(label, className);
    }
    return label.toString();
  }

  protected void appendSuffix(StringBuilder label, String suffix) {
    label.append("[");
    label.append(suffix);
    label.append("]");
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent event) {
  }
}