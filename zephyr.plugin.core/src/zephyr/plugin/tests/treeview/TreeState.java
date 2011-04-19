package zephyr.plugin.tests.treeview;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;

import zephyr.plugin.core.api.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;

public class TreeState implements TreeListener {
  static final private String MementoRootType = "Expanded";
  static final private String MementoIdentifierID = "Identifier";

  private final ItemProvider itemProvider;
  private final Set<String> expanded = new HashSet<String>();
  private final Set<TreeItem> nodesToExpand = new HashSet<TreeItem>();

  public TreeState(ItemProvider itemProvider) {
    this.itemProvider = itemProvider;
  }

  @Override
  public void treeCollapsed(TreeEvent event) {
    CodeNode codeNode = (CodeNode) ((TreeItem) event.item).getData();
    for (String identifier : new ArrayList<String>(expanded)) {
      if (identifier.startsWith(pathToId(codeNode.path())))
        expanded.remove(identifier);
    }
  }

  private String pathToId(String[] path) {
    StringBuilder id = new StringBuilder();
    for (String e : path)
      id.append(e);
    return id.toString();
  }

  @Override
  public void treeExpanded(TreeEvent event) {
    final TreeItem root = (TreeItem) event.item;
    CodeNode codeNode = (CodeNode) root.getData();
    expanded.add(pathToId(codeNode.path()));
    buildChildrenIFN(root);
    expandNodes();
  }

  private void buildChildrenIFN(TreeItem root) {
    for (TreeItem item : root.getItems()) {
      if (item.getData() != null)
        return;
      item.dispose();
    }
    itemProvider.createChildrenItems(root);
  }

  public void nodeCreated(TreeItem treeItem) {
    CodeNode codeNode = (CodeNode) treeItem.getData();
    String nodeIdentifier = pathToId(codeNode.path());
    for (String identifier : expanded)
      if (identifier.startsWith(nodeIdentifier)) {
        nodeToExpand(treeItem);
        break;
      }
    if (codeNode instanceof ClockNode)
      nodeToExpand(treeItem);
  }

  private void nodeToExpand(TreeItem treeItem) {
    nodesToExpand.add(treeItem);
  }

  public void expandNodes() {
    List<TreeItem> cache = new ArrayList<TreeItem>();
    while (!nodesToExpand.isEmpty()) {
      List<TreeItem> thisLayer = new ArrayList<TreeItem>(nodesToExpand);
      cache.addAll(thisLayer);
      nodesToExpand.clear();
      for (TreeItem treeItem : thisLayer)
        buildChildrenIFN(treeItem);
    }
    for (TreeItem treeItem : cache)
      treeItem.setExpanded(true);
  }

  public void init(IMemento memento) {
    if (memento == null)
      return;
    IMemento rootNode = memento.getChild(MementoRootType);
    if (rootNode == null)
      return;
    IMemento[] children = rootNode.getChildren(MementoIdentifierID);
    if (children == null)
      return;
    for (IMemento child : children)
      expanded.add(child.getID());
  }

  public void saveState(IMemento memento) {
    IMemento rootNode = memento.createChild(MementoRootType);
    for (String identifier : expanded)
      rootNode.createChild(MementoIdentifierID, identifier);
  }
}
