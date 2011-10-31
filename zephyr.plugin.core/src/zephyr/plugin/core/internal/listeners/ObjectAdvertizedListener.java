package zephyr.plugin.core.internal.listeners;

import zephyr.plugin.core.api.Zephyr.AdvertisementInfo;
import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;
import zephyr.plugin.core.events.AdvertizeEvent;
import zephyr.plugin.core.internal.ZephyrPluginCore;

public class ObjectAdvertizedListener implements EventListener {
  @Override
  public void listen(Event event) {
    AdvertisementInfo eventInfo = ((AdvertizeEvent) event).info();
    ZephyrPluginCore.syncCode().parse(eventInfo.clock, eventInfo.advertised);
  }
}
