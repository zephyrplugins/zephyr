package zephyr.plugin.core.internal.views;

import java.util.List;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.traverser.Traverser;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.synchronization.binding.ViewBinder;
import zephyr.plugin.core.internal.synchronization.providers.ViewProviderReference;

public class PopupViewTraverser implements Traverser {
  private final ViewBinder viewBinder;

  public PopupViewTraverser() {
    viewBinder = ZephyrPluginCore.viewBinder();
  }

  @Override
  public boolean inNode(CodeNode codeNode) {
    if (codeNode instanceof ClassNode && !((ClassNode) codeNode).isAdvertized())
      return false;
    List<ViewProviderReference> providers = viewBinder.findViewProviders(codeNode);
    for (ViewProviderReference provider : providers)
      if (provider.popUpView())
        viewBinder.displayAndBindView(codeNode, provider.viewID());
    return true;
  }

  @Override
  public void outNode(CodeNode codeNode) {
  }
}
