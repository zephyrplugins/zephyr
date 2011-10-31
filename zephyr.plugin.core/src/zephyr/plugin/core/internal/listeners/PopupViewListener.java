package zephyr.plugin.core.internal.listeners;

import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;
import zephyr.plugin.core.events.CodeParsedEvent;
import zephyr.plugin.core.internal.views.PopupViewTraverser;

public class PopupViewListener implements EventListener {
  @Override
  public void listen(Event event) {
    CodeParsedEvent eventInfo = (CodeParsedEvent) event;
    CodeTrees.traverse(new PopupViewTraverser(), eventInfo.node());
  }
}
