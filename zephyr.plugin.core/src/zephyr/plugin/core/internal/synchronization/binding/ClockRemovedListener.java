package zephyr.plugin.core.internal.synchronization.binding;

import zephyr.plugin.core.async.events.CastedEventListener;
import zephyr.plugin.core.events.ClockEvent;
import zephyr.plugin.core.internal.ZephyrPluginCore;

public class ClockRemovedListener extends CastedEventListener<ClockEvent> {
  @Override
  public void listenEvent(ClockEvent event) {
    ViewBinder viewBinder = ZephyrPluginCore.viewBinder();
    ClockViews clockViews = viewBinder.removeClock(event.clock());
    clockViews.dispose();
  }
}
