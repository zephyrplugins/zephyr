package zephyr.plugin.core.internal.treeview;

import org.eclipse.swt.widgets.TreeItem;

public interface ItemProvider {
  void createChildrenItems(TreeItem root);
}
