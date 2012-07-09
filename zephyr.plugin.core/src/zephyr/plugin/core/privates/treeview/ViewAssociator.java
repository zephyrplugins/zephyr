package zephyr.plugin.core.privates.treeview;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.privates.ZephyrPluginCore;
import zephyr.plugin.core.privates.synchronization.binding.ViewBinder;
import zephyr.plugin.core.privates.synchronization.providers.ViewProviderReference;

public class ViewAssociator {
  public ViewAssociator() {
  }

  public void showSelection(CodeNode[] codeNodes, String defaultView) {
    Set<ViewProviderReference> providers = buildProviders(codeNodes);
    for (ViewProviderReference reference : providers) {
      final String viewID = reference.viewID();
      if (viewID == null || viewID.isEmpty())
        continue;
      if (defaultView != null && !defaultView.equals(viewID))
        continue;
      displayInView(reference, filter(reference, codeNodes));
    }
  }

  private static void displayInView(final ViewProviderReference reference, final List<CodeNode> codeNodes) {
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

  private static List<CodeNode> filter(ViewProviderReference reference, CodeNode[] codeNodes) {
    List<CodeNode> result = new ArrayList<CodeNode>();
    for (CodeNode codeNode : codeNodes)
      if (reference.provider().canViewDraw(codeNode))
        result.add(codeNode);
    return result;
  }

  public Set<ViewProviderReference> buildProviders(CodeNode[] codeNodes) {
    final ViewBinder viewBinder = ZephyrPluginCore.viewBinder();
    Set<ViewProviderReference> providers = new LinkedHashSet<ViewProviderReference>();
    for (CodeNode codeNode : codeNodes)
      providers.addAll(viewBinder.findViewProviders(codeNode));
    return providers;
  }
}
