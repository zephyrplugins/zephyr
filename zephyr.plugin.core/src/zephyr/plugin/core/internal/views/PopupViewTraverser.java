package zephyr.plugin.core.internal.views;

import java.util.List;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.traverser.Traverser;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.synchronization.ViewBinder;
import zephyr.plugin.core.internal.synchronization.providers.ViewProviderReference;

public class PopupViewTraverser implements Traverser {
  private final Clock clock;
  private final ViewBinder viewBinder;

  public PopupViewTraverser(Clock clock) {
    this.clock = clock;
    viewBinder = ZephyrPluginCore.viewBinder();
  }

  @Override
  public boolean inNode(CodeNode codeNode) {
    List<ViewProviderReference> providers = viewBinder.findViewProviders(codeNode);
    for (ViewProviderReference provider : providers)
      if (provider.popUpView())
        viewBinder.displayAndBindView(clock, codeNode, provider.viewID());
    return true;
  }

  @Override
  public void outNode(CodeNode codeNode) {
  }
}
