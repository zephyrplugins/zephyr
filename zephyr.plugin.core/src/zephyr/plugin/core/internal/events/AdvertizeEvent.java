package zephyr.plugin.core.internal.events;

import zephyr.plugin.core.api.Zephyr.AdvertisementInfo;
import zephyr.plugin.core.internal.async.events.Event;

public class AdvertizeEvent implements Event {
  private static final String ID = "zephyr.event.advertize";
  private final AdvertisementInfo info;

  public AdvertizeEvent(AdvertisementInfo info) {
    this.info = info;
  }

  public AdvertisementInfo info() {
    return info;
  }

  @Override
  public String id() {
    return ID;
  }
}
