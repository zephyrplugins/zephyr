package zephyr.plugin.core.privates.treeview;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import zephyr.plugin.core.api.internal.codeparser.codetree.AbstractPrimitives;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.internal.SyncCode;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.EventListener;
import zephyr.plugin.core.internal.async.listeners.UIListener;
import zephyr.plugin.core.internal.events.ClockEvent;
import zephyr.plugin.core.internal.events.CodeStructureEvent;
import zephyr.plugin.core.internal.views.ViewWithControl;

public class StructureExplorerView extends ViewPart implements ItemProvider, ViewWithControl {
  static final public String ViewID = "zephyr.plugin.core.treeview";
  private Tree tree;
  private final SyncCode codeParser;
  private final IconDatabase iconDatabase = new IconDatabase();
  final Map<ClockNode, TreeItem> clockItems = new HashMap<ClockNode, TreeItem>();
  private final RootClassNodeListener classNodeListener;
  final TreeState treeState = new TreeState(this);
  private final SelectionListener selectionListener = new SelectionTreeListener();
  private MouseTreeListener mouseListener = null;
  private final ViewAssociator viewAssociator = new ViewAssociator();
  private final EventListener onClockNodeRemoved = new UIListener() {
    @Override
    public void listenInUIThread(Event event) {
      CodeStructureEvent structureEvent = (CodeStructureEvent) event;
      TreeItem treeItem = clockItems.get(structureEvent.clockNode());
      if (treeItem != null)
        treeItem.dispose();
    }
  };

  public StructureExplorerView() {
    codeParser = ZephyrSync.syncCode();
    classNodeListener = new RootClassNodeListener(this);
    ZephyrSync.busEvent().register(CodeStructureEvent.ParsedID, classNodeListener);
    ZephyrSync.busEvent().register(CodeStructureEvent.RemovedID, onClockNodeRemoved);
  }

  @Override
  public void createPartControl(Composite parent) {
    GridLayout parentLayout = new GridLayout(1, false);
    parentLayout.marginWidth = 0;
    parentLayout.marginHeight = 0;
    parent.setLayout(parentLayout);
    tree = new Tree(parent, SWT.MULTI);
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    new ExplorerDragSource().setTree(tree);
    buildRootNode();
    tree.addSelectionListener(selectionListener);
    tree.addTreeListener(treeState);
    mouseListener = new MouseTreeListener(tree, viewAssociator);
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
    if (codeNode instanceof ClassNode && codeNode.label().isEmpty())
      return ((ClassNode) codeNode).instance().getClass().getSimpleName();
    if (codeNode instanceof AbstractPrimitives)
      return codeNode.label() + "[" + ((AbstractPrimitives) codeNode).size() + "]";
    return codeNode.label();
  }

  private static boolean hasChildren(CodeNode codeNode) {
    if (!(codeNode instanceof ParentNode))
      return false;
    return ((ParentNode) codeNode).nbChildren() > 0;
  }

  private void buildRootNode() {
    for (ClockNode clockNode : codeParser.getClockNodes())
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
    ZephyrSync.busEvent().unregister(ClockEvent.RemovedID, onClockNodeRemoved);
    ZephyrSync.busEvent().unregister(CodeStructureEvent.ParsedID, classNodeListener);
    mouseListener.dispose();
    iconDatabase.dispose();
    tree.dispose();
    clockItems.clear();
    super.dispose();
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
