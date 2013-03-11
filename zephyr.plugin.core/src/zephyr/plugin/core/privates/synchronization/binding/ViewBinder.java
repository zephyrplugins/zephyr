package zephyr.plugin.core.privates.synchronization.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.ui.IViewReference;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.views.SyncView;
import zephyr.plugin.core.privates.synchronization.providers.ViewProviderReference;
import zephyr.plugin.core.privates.synchronization.providers.ViewProviders;
import zephyr.plugin.core.privates.synchronization.tasks.ViewReference;
import zephyr.plugin.core.privates.synchronization.viewfinder.ViewFinder;

public class ViewBinder {
  protected final Map<Clock, ClockViews> clockToView = Collections.synchronizedMap(new HashMap<Clock, ClockViews>());
  private final ViewProviders viewProviders = new ViewProviders();
  private final Map<String, ViewFinder> viewFinders = new HashMap<String, ViewFinder>();

  private static void bindWithNewView(CodeNode[] codeNodes, ViewFinder viewFinder) {
    CodeNode[] remainToDisplay = codeNodes;
    while (remainToDisplay.length > 0) {
      ViewReference viewRef = viewFinder.provideNewView();
      if (viewRef == null)
        return;
      boolean[] displayed = viewRef.provide(remainToDisplay);
      int nbToDisplay = remainToDisplay.length;
      remainToDisplay = removeDisplayed(displayed, remainToDisplay);
      if (nbToDisplay == remainToDisplay.length)
        break;
    }
  }

  private static CodeNode[] bindWithOpenedViews(CodeNode[] codeNodes, ViewFinder viewFinder) {
    CodeNode[] remainToDisplay = codeNodes;
    for (IViewReference reference : viewFinder.existingViews()) {
      ViewReference viewRef = viewFinder.showView(reference);
      if (viewRef == null)
        continue;
      boolean[] displayed = viewRef.provide(remainToDisplay);
      remainToDisplay = removeDisplayed(displayed, remainToDisplay);
      if (remainToDisplay.length == 0)
        break;
    }
    return remainToDisplay;
  }

  private static CodeNode[] removeDisplayed(boolean[] displayed, CodeNode[] codeNodes) {
    List<CodeNode> remainToDisplay = new ArrayList<CodeNode>();
    for (int i = 0; i < codeNodes.length; i++)
      if (!displayed[i])
        remainToDisplay.add(codeNodes[i]);
    CodeNode[] result = new CodeNode[remainToDisplay.size()];
    remainToDisplay.toArray(result);
    return result;
  }

  // displayAndBindView need to call the ViewBinder from within
  // a UI thread inside a syncExec. Therefore, this method cannot be
  // synchronized
  public void displayAndBindView(CodeNode[] codeNodes, String viewID) {
    ViewFinder viewFinder = viewFinders.get(viewID);
    if (viewFinder == null) {
      viewFinder = new ViewFinder(viewID);
      viewFinders.put(viewID, viewFinder);
    }
    CodeNode[] remainToDisplay = bindWithOpenedViews(codeNodes, viewFinder);
    if (remainToDisplay.length == 0)
      return;
    bindWithNewView(remainToDisplay, viewFinder);
  }

  public void displayAndBindView(CodeNode codeNodes, String viewID) {
    displayAndBindView(new CodeNode[] { codeNodes }, viewID);
  }

  public List<ViewProviderReference> findViewProviders(CodeNode codeNode) {
    return viewProviders.findViews(codeNode);
  }

  void register(ClockViews clockViews) {
    ClockViews previous = clockToView.put(clockViews.clock(), clockViews);
    assert previous == null;
  }

  public ClockViews removeClock(Clock clock) {
    return clockToView.remove(clock);
  }

  public void bind(Clock clock, SyncView view) {
    ClockViews clockViews = clockToView.get(clock);
    if (clockViews == null)
      return;
    clockViews.addView(view);
  }

  public void unbind(Clock clock, SyncView view) {
    ClockViews clockViews = clockToView.get(clock);
    if (clockViews == null)
      return;
    clockViews.removeView(view);
  }

  public void disposeView(SyncView view) {
    for (ClockViews clockViews : clockToView.values())
      clockViews.removeView(view);
  }

  public ClockViews clockViews(Clock clock) {
    return clockToView.get(clock);
  }

  public void popup(CodeNode codeNode) {
    List<ViewProviderReference> providers = findViewProviders(codeNode);
    for (ViewProviderReference provider : providers)
      displayAndBindView(codeNode, provider.viewID());
  }
}
