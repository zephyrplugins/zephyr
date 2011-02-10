package zephyr.plugin.core.api;

import zephyr.plugin.core.api.advertisement.Advertisement;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Timed;

public class Zephyr {
  static private Advertisement advertisement = new Advertisement();

  static public Advertisement advertisement() {
    return advertisement;
  }

  static public void advertise(Timed timed) {
    advertise(timed, null);
  }

  static public void advertise(Timed timed, Object info) {
    advertise(timed.clock(), timed, info);
  }

  static public void advertise(Clock clock, Object drawn) {
    advertise(clock, drawn, null);
  }

  static public void advertise(Clock clock, Object drawn, Object info) {
    advertisement.parse(clock, drawn, info);
  }
}
