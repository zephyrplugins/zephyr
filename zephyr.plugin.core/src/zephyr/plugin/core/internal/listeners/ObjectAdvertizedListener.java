package zephyr.plugin.core.internal.listeners;

import zephyr.plugin.core.api.Zephyr.AdvertisementInfo;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.async.Event;
import zephyr.plugin.core.async.EventListener;
import zephyr.plugin.core.events.AdvertizeEvent;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.views.PopupViewTraverser;

public class ObjectAdvertizedListener implements EventListener {
  @Override
  public void listen(Event event) {
    AdvertisementInfo eventInfo = ((AdvertizeEvent) event).info();
    Clock clock = eventInfo.clock;
    CodeNode[] children = ZephyrPluginCore.syncCode().parse(clock, eventInfo.advertised);
    PopupViewTraverser traverser = new PopupViewTraverser();
    for (CodeNode node : children)
      CodeTrees.traverse(traverser, node);
  }
}
