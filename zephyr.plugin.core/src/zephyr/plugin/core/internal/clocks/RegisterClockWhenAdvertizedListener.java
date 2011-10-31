package zephyr.plugin.core.internal.clocks;

import zephyr.plugin.core.async.events.CastedEventListener;
import zephyr.plugin.core.events.AdvertizeEvent;
import zephyr.plugin.core.internal.ZephyrPluginCore;

public class RegisterClockWhenAdvertizedListener extends CastedEventListener<AdvertizeEvent> {
  @Override
  public void listenEvent(AdvertizeEvent event) {
    ZephyrPluginCore.clocks().register(event.info().clock);
  }
}
