package zephyr.plugin.core.internal.clocks;

import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;
import zephyr.plugin.core.events.ClockEvent;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.utils.Helper;

public class SuspendClockWhenAddedListener implements EventListener {
  @Override
  public void listen(Event eventInfo) {
    ClockEvent event = (ClockEvent) eventInfo;
    if (!Helper.booleanState("zephyr.plugin.core.commands.startsuspended", false))
      return;
    ZephyrPluginCore.control().suspendClock(event.clock());
  }
}
