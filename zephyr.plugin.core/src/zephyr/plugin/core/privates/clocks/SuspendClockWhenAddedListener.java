package zephyr.plugin.core.privates.clocks;

import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.EventListener;
import zephyr.plugin.core.internal.events.ClockEvent;
import zephyr.plugin.core.internal.utils.Helper;
import zephyr.plugin.core.privates.ZephyrPluginCore;

public class SuspendClockWhenAddedListener implements EventListener {
  @Override
  public void listen(Event eventInfo) {
    ClockEvent event = (ClockEvent) eventInfo;
    if (!Helper.booleanState("zephyr.plugin.core.commands.startsuspended", false))
      return;
    ZephyrPluginCore.control().suspendClock(event.clock());
  }
}
