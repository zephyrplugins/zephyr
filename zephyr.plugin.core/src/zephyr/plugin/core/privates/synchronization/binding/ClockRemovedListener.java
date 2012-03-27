package zephyr.plugin.core.privates.synchronization.binding;

import zephyr.plugin.core.internal.async.events.CastedEventListener;
import zephyr.plugin.core.internal.events.ClockEvent;
import zephyr.plugin.core.privates.ZephyrPluginCore;

public class ClockRemovedListener extends CastedEventListener<ClockEvent> {
  @Override
  public void listenEvent(ClockEvent event) {
    ViewBinder viewBinder = ZephyrPluginCore.viewBinder();
    ClockViews clockViews = viewBinder.removeClock(event.clock());
    clockViews.dispose();
  }
}
