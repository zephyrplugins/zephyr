package zephyr.plugin.core.internal.listeners;

import zephyr.plugin.core.async.events.CastedEventListener;
import zephyr.plugin.core.events.ClockEvent;
import zephyr.plugin.core.internal.ZephyrPluginCore;

public class RemoveNodeCodeWhenClockRemovedListener extends CastedEventListener<ClockEvent> {
  @Override
  public void listenEvent(ClockEvent event) {
    ZephyrPluginCore.syncCode().removeClockNode(event.clock());
  }
}
