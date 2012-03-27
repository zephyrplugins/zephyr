package zephyr.plugin.core.privates.clocks;

import zephyr.plugin.core.internal.async.events.CastedEventListener;
import zephyr.plugin.core.internal.events.AdvertizeEvent;
import zephyr.plugin.core.privates.ZephyrPluginCore;

public class RegisterClockWhenAdvertizedListener extends CastedEventListener<AdvertizeEvent> {
  @Override
  public void listenEvent(AdvertizeEvent event) {
    ZephyrPluginCore.clocks().register(event.info().clock);
  }
}
