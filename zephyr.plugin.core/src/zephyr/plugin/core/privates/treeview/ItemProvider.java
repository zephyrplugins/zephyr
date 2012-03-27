package zephyr.plugin.core.privates.treeview;

import org.eclipse.swt.widgets.TreeItem;

public interface ItemProvider {
  void createChildrenItems(TreeItem root);
}
