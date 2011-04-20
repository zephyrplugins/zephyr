package zephyr.plugin.core.internal.treeview;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.synchronization.ViewBinder;
import zephyr.plugin.core.internal.synchronization.providers.ViewProviderReference;

public class MouseTreeListener implements MouseListener {
  @Override
  public void mouseDoubleClick(MouseEvent event) {
    Tree tree = (Tree) event.widget;
    TreeItem[] treeItems = tree.getSelection();
    CodeNode[] codeNodes = new CodeNode[treeItems.length];
    for (int i = 0; i < codeNodes.length; i++)
      codeNodes[i] = (CodeNode) treeItems[i].getData();
    Set<ViewProviderReference> providers = buildProviders(codeNodes);
    for (ViewProviderReference reference : providers) {
      final String viewID = reference.viewID();
      if (viewID == null || viewID.isEmpty())
        continue;
      displayInView(reference, filter(reference, codeNodes));
    }
  }

  private void displayInView(final ViewProviderReference reference, final List<CodeNode> codeNodes) {
    ZephyrPluginCore.viewScheduler().schedule(new Runnable() {
      @Override
      public void run() {
        ViewBinder viewBinder = ZephyrPluginCore.viewBinder();
        CodeNode[] array = new CodeNode[codeNodes.size()];
        codeNodes.toArray(array);
        viewBinder.displayAndBindView(array, reference.viewID());
      }
    });
  }

  private List<CodeNode> filter(ViewProviderReference reference, CodeNode[] codeNodes) {
    List<CodeNode> result = new ArrayList<CodeNode>();
    for (CodeNode codeNode : codeNodes)
      if (reference.provider().canViewDraw(codeNode))
        result.add(codeNode);
    return result;
  }

  private Set<ViewProviderReference> buildProviders(CodeNode[] codeNodes) {
    final ViewBinder viewBinder = ZephyrPluginCore.viewBinder();
    Set<ViewProviderReference> providers = new LinkedHashSet<ViewProviderReference>();
    for (CodeNode codeNode : codeNodes)
      providers.addAll(viewBinder.findViewProviders(codeNode));
    return providers;
  }

  @Override
  public void mouseDown(MouseEvent event) {
  }

  @Override
  public void mouseUp(MouseEvent event) {
  }
}
