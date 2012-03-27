package zephyr.plugin.core.privates.listeners;

import zephyr.plugin.core.internal.async.events.CastedEventListener;
import zephyr.plugin.core.internal.events.ClockEvent;
import zephyr.plugin.core.privates.ZephyrPluginCore;

public class RemoveNodeCodeWhenClockRemovedListener extends CastedEventListener<ClockEvent> {
  @Override
  public void listenEvent(ClockEvent event) {
    ZephyrPluginCore.syncCode().removeClockNode(event.clock());
  }
}
