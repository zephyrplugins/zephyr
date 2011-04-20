package zephyr.plugin.core.internal.treeview;

import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.synchronization.ViewBinder;
import zephyr.plugin.core.internal.synchronization.providers.ViewProviderReference;

public class MouseTreeListener implements MouseListener {
  @Override
  public void mouseDoubleClick(MouseEvent event) {
    Tree tree = (Tree) event.widget;
    TreeItem treeItem = tree.getItem(new Point(event.x, event.y));
    if (treeItem == null)
      return;
    final CodeNode codeNode = (CodeNode) treeItem.getData();
    if (codeNode == null)
      return;
    final ViewBinder viewBinder = ZephyrPluginCore.viewBinder();
    List<ViewProviderReference> providers = viewBinder.findViewProviders(codeNode);
    for (ViewProviderReference reference : providers) {
      final String viewID = reference.viewID();
      if (viewID == null || viewID.isEmpty())
        continue;
      ZephyrPluginCore.viewScheduler().schedule(new Runnable() {
        @Override
        public void run() {
          viewBinder.displayAndBindView(CodeTrees.clockOf(codeNode), codeNode, viewID);
        }
      });
    }
  }

  @Override
  public void mouseDown(MouseEvent event) {
  }

  @Override
  public void mouseUp(MouseEvent event) {
  }
}
