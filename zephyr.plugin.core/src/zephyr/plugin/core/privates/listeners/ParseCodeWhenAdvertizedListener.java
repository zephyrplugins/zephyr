package zephyr.plugin.core.privates.listeners;

import zephyr.plugin.core.api.Zephyr.AdvertisementInfo;
import zephyr.plugin.core.internal.async.events.CastedEventListener;
import zephyr.plugin.core.internal.events.AdvertizeEvent;
import zephyr.plugin.core.privates.ZephyrPluginCore;

public class ParseCodeWhenAdvertizedListener extends CastedEventListener<AdvertizeEvent> {
  @Override
  public void listenEvent(AdvertizeEvent event) {
    AdvertisementInfo eventInfo = event.info();
    if (eventInfo.advertised == null)
      return;
    ZephyrPluginCore.syncCode().parse(eventInfo.clock, eventInfo.advertised, eventInfo.label);
  }
}
