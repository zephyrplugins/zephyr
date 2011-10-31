package zephyr.plugin.core.internal.synchronization.binding;

import zephyr.plugin.core.async.events.CastedEventListener;
import zephyr.plugin.core.events.ClockEvent;
import zephyr.plugin.core.internal.ZephyrPluginCore;

public class ClockAddedListener extends CastedEventListener<ClockEvent> {
  @Override
  public void listenEvent(ClockEvent event) {
    ClockViews clockViews = new ClockViews(event.clock());
    ZephyrPluginCore.viewBinder().register(clockViews);
  }
}
