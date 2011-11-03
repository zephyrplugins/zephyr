package zephyr.plugin.core.internal.listeners;

import zephyr.plugin.core.api.Zephyr.AdvertisementInfo;
import zephyr.plugin.core.async.events.CastedEventListener;
import zephyr.plugin.core.events.AdvertizeEvent;
import zephyr.plugin.core.internal.ZephyrPluginCore;

public class ParseCodeWhenAdvertizedListener extends CastedEventListener<AdvertizeEvent> {
  @Override
  public void listenEvent(AdvertizeEvent event) {
    AdvertisementInfo eventInfo = event.info();
    if (eventInfo.advertised == null)
      return;
    ZephyrPluginCore.syncCode().parse(eventInfo.clock, eventInfo.advertised);
  }
}
