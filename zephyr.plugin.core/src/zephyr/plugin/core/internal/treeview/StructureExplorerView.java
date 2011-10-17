package zephyr.plugin.core.internal.treeview;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import zephyr.ZephyrCore;
import zephyr.ZephyrSync;
import zephyr.plugin.core.SyncCode;
import zephyr.plugin.core.api.codeparser.codetree.AbstractPrimitives;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.views.ViewWithControl;

public class StructureExplorerView extends ViewPart implements ItemProvider, ViewWithControl {
  static final public String ViewID = "zephyr.plugin.core.treeview";
  private Tree tree;
  private final SyncCode codeParser;
  private final IconDatabase iconDatabase = new IconDatabase();
  final Map<ClockNode, TreeItem> clockItems = new HashMap<ClockNode, TreeItem>();
  private final Listener<CodeNode> classNodeListener;
  final TreeState treeState = new TreeState(this);
  private final SelectionListener selectionListener = new SelectionTreeListener();
  private final MouseTreeListener mouseListener = new MouseTreeListener();
  private final Listener<Clock> onClockRemoved = new Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      removeClock(clock);
    }
  };

  public StructureExplorerView() {
    codeParser = ZephyrCore.syncCode();
    classNodeListener = new RootClassNodeListener(this);
    codeParser.onParse.connect(classNodeListener);
    ZephyrSync.onClockRemoved().connect(onClockRemoved);
  }

  protected void removeClock(final Clock clock) {
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        ClockNode clockNode = ZephyrCore.syncCode().clockNode(clock);
        TreeItem treeItem = clockItems.get(clockNode);
        if (treeItem != null)
          treeItem.dispose();
      }
    });
  }

  @Override
  public void createPartControl(Composite parent) {
    GridLayout parentLayout = new GridLayout(1, false);
    parent.setLayout(parentLayout);
    tree = new Tree(parent, SWT.MULTI);
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    new ExplorerDragSource().setTree(tree);
    buildRootNode();
    tree.addSelectionListener(selectionListener);
    tree.addTreeListener(treeState);
    tree.addMouseListener(mouseListener);
    new TooltipManager(tree);
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
    item.setText(itemLabel(codeNode));
    item.setData(codeNode);
    iconDatabase.setImage(item);
    treeState.nodeCreated(item);
    return item;
  }

  protected String itemLabel(CodeNode codeNode) {
    if (codeNode.parent() instanceof ClockNode)
      return ((ClassNode) codeNode).instance().getClass().getSimpleName();
    if (codeNode instanceof AbstractPrimitives)
      return codeNode.label() + "[" + ((AbstractPrimitives) codeNode).size() + "]";
    return codeNode.label();
  }

  private boolean hasChildren(CodeNode codeNode) {
    if (!(codeNode instanceof ParentNode))
      return false;
    return ((ParentNode) codeNode).nbChildren() > 0;
  }

  private void buildRootNode() {
    for (ClockNode clockNode : codeParser.clockNodes())
      for (int i = 0; i < clockNode.nbChildren(); i++)
        registerClockChildNode(clockNode.getChild(i));
  }

  void registerClockChildNode(CodeNode childNode) {
    TreeItem clockItem = getClockItem((ClockNode) childNode.parent());
    TreeItem classItem = nodeToTreeItem(clockItem, childNode);
    if (hasChildren(childNode))
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
    ZephyrSync.onClockRemoved().disconnect(onClockRemoved);
    codeParser.onParse.disconnect(classNodeListener);
    super.dispose();
    iconDatabase.dispose();
    tree.dispose();
    clockItems.clear();
  }

  static public CodeNode[] getSelection(Tree tree) {
    TreeItem[] treeItems = tree.getSelection();
    CodeNode[] codeNodes = new CodeNode[treeItems.length];
    for (int i = 0; i < codeNodes.length; i++)
      codeNodes[i] = (CodeNode) treeItems[i].getData();
    return codeNodes;
  }

  @Override
  public Control control() {
    return tree.getParent();
  }
}
