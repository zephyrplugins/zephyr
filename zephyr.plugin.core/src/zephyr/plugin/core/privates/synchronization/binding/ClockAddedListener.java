package zephyr.plugin.core.privates.synchronization.binding;

import zephyr.plugin.core.internal.async.events.CastedEventListener;
import zephyr.plugin.core.internal.events.ClockEvent;
import zephyr.plugin.core.privates.ZephyrPluginCore;

public class ClockAddedListener extends CastedEventListener<ClockEvent> {
  @Override
  public void listenEvent(ClockEvent event) {
    ClockViews clockViews = new ClockViews(event.clock());
    ZephyrPluginCore.viewBinder().register(clockViews);
  }
}
