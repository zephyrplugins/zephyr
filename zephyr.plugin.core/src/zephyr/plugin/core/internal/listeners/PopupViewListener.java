package zephyr.plugin.core.internal.listeners;

import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;
import zephyr.plugin.core.events.CodeStructureEvent;
import zephyr.plugin.core.internal.views.PopupViewTraverser;

public class PopupViewListener implements EventListener {
  @Override
  public void listen(Event event) {
    CodeStructureEvent eventInfo = (CodeStructureEvent) event;
    CodeTrees.traverse(new PopupViewTraverser(), eventInfo.node());
  }
}
