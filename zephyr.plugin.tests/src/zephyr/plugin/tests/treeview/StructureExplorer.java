package zephyr.plugin.tests.treeview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.helpers.ImageManager;
import zephyr.plugin.tests.ZephyrTestsPlugin;
import zephyr.plugin.tests.codeparser.ClassNode;
import zephyr.plugin.tests.codeparser.ClockNode;
import zephyr.plugin.tests.codeparser.CodeNode;
import zephyr.plugin.tests.codeparser.CodeParser;

public class StructureExplorer extends ViewPart {
  Tree tree;
  private final CodeParser codeParser;
  private final ImageManager imageManager = new ImageManager();
  private final Map<ClockNode, TreeItem> clockItems = new HashMap<ClockNode, TreeItem>();
  private final Listener<ClassNode> classNodeListener = new Listener<ClassNode>() {
    @Override
    public void listen(final ClassNode classNode) {
      if (tree == null)
        return;
      Display.getDefault().asyncExec(new Runnable() {
        @Override
        public void run() {
          registerClassNode(classNode);
          expandNodes();
        }
      });
    }
  };
  private final List<TreeItem> nodesToExpand = new ArrayList<TreeItem>();

  public StructureExplorer() {
    codeParser = ZephyrTestsPlugin.codeParser();
    codeParser.onParse.connect(classNodeListener);
  }

  @Override
  public void createPartControl(Composite parent) {
    GridLayout parentLayout = new GridLayout(1, false);
    parent.setLayout(parentLayout);
    tree = new Tree(parent, 0);
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    buildRootNode();
    expandNodes();
  }

  void expandNodes() {
    for (TreeItem item : nodesToExpand)
      item.setExpanded(true);
    nodesToExpand.clear();
  }

  private void buildRootNode() {
    for (ClockNode clockNode : codeParser.clockNodes())
      for (CodeNode codeNode : clockNode.children())
        registerClassNode((ClassNode) codeNode);
  }

  void registerClassNode(ClassNode classNode) {
    TreeItem clockItem = getClockItem(classNode.root());
    TreeItem classItem = new TreeItem(clockItem, 0);
    classItem.setText(classNode.label());
    classItem.setData(classNode);
    new TreeItem(classItem, 0);
  }

  private TreeItem getClockItem(ClockNode clockNode) {
    TreeItem clockItem = clockItems.get(clockNode);
    if (clockItem == null) {
      clockItem = new TreeItem(tree, SWT.NONE);
      clockItem.setText(Labels.label(clockNode));
      clockItem.setImage(imageManager.image(ZephyrCore.PluginID, "icons/view_clocks.png"));
      clockItem.setData(clockNode);
      nodesToExpand.add(clockItem);
      clockItems.put(clockNode, clockItem);
    }
    return clockItem;
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void dispose() {
    codeParser.onParse.disconnect(classNodeListener);
    super.dispose();
    imageManager.dispose();
    tree.dispose();
    clockItems.clear();
  }
}
