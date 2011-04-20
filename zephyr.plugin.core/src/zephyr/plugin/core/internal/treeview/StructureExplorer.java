package zephyr.plugin.core.internal.treeview;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import zephyr.ZephyrCore;
import zephyr.plugin.core.SyncCode;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.signals.Listener;

public class StructureExplorer extends ViewPart implements ItemProvider {
  private Tree tree;
  private final SyncCode codeParser;
  private final IconDatabase iconDatabase = new IconDatabase();
  private final Map<ClockNode, TreeItem> clockItems = new HashMap<ClockNode, TreeItem>();
  private final Listener<ClassNode> classNodeListener;
  final TreeState treeState = new TreeState(this);
  private final SelectionListener selectionListener = new SelectionTreeListener();
  private final MouseTreeListener mouseListener = new MouseTreeListener();

  public StructureExplorer() {
    codeParser = ZephyrCore.syncCode();
    classNodeListener = new RootClassNodeListener(this);
    codeParser.onParse.connect(classNodeListener);
  }

  @Override
  public void createPartControl(Composite parent) {
    GridLayout parentLayout = new GridLayout(1, false);
    parent.setLayout(parentLayout);
    tree = new Tree(parent, 0);
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    buildRootNode();
    tree.addSelectionListener(selectionListener);
    tree.addTreeListener(treeState);
    tree.addMouseListener(mouseListener);
    treeState.expandNodes();
  }

  @Override
  public void createChildrenItems(TreeItem root) {
    ParentNode parentNode = (ParentNode) root.getData();
    for (int i = 0; i < parentNode.nbChildren(); i++) {
      CodeNode child = parentNode.getChild(i);
      TreeItem item = nodeToTreeItem(root, child);
      if (hasChildren(child))
        new TreeItem(item, 0);
    }
  }

  private TreeItem nodeToTreeItem(TreeItem root, CodeNode codeNode) {
    return setTreeItem(new TreeItem(root, SWT.NONE), codeNode);
  }

  private TreeItem nodeToTreeItem(Tree tree, CodeNode codeNode) {
    return setTreeItem(new TreeItem(tree, SWT.NONE), codeNode);
  }

  private TreeItem setTreeItem(TreeItem item, CodeNode codeNode) {
    if (codeNode.parent() instanceof ClockNode)
      item.setText(((ClassNode) codeNode).instance().getClass().getSimpleName());
    else
      item.setText(codeNode.label());
    item.setData(codeNode);
    iconDatabase.setImage(item);
    treeState.nodeCreated(item);
    return item;
  }

  private boolean hasChildren(CodeNode codeNode) {
    if (!(codeNode instanceof ParentNode))
      return false;
    return ((ParentNode) codeNode).nbChildren() > 0;
  }

  private void buildRootNode() {
    for (ClockNode clockNode : codeParser.clockNodes())
      for (int i = 0; i < clockNode.nbChildren(); i++)
        registerClassNode(clockNode.getChild(i));
  }

  void registerClassNode(ClassNode classNode) {
    TreeItem clockItem = getClockItem((ClockNode) classNode.parent());
    TreeItem classItem = nodeToTreeItem(clockItem, classNode);
    if (hasChildren(classNode))
      new TreeItem(classItem, 0);
  }

  private TreeItem getClockItem(ClockNode clockNode) {
    TreeItem clockItem = clockItems.get(clockNode);
    if (clockItem == null) {
      clockItem = nodeToTreeItem(tree, clockNode);
      clockItems.put(clockNode, clockItem);
      treeState.nodeCreated(clockItem);
    }
    return clockItem;
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    treeState.init(memento);
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    treeState.saveState(memento);
  }

  public Tree tree() {
    return tree;
  }

  public TreeState treeState() {
    return treeState;
  }

  @Override
  public void dispose() {
    codeParser.onParse.disconnect(classNodeListener);
    super.dispose();
    iconDatabase.dispose();
    tree.dispose();
    clockItems.clear();
  }
}
