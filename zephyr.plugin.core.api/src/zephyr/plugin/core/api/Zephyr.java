package zephyr.plugin.core.api;

import zephyr.plugin.core.api.advertizement.Advertizement;
import zephyr.plugin.core.api.synchronization.Clock;

public class Zephyr {
  static private Advertizement advertizement = new Advertizement();

  static public Advertizement advertizement() {
    return advertizement;
  }

  static public void advertize(Clock clock, Object drawn) {
    advertize(clock, drawn, null);
  }

  static public void advertize(Clock clock, Object drawn, Object info) {
    advertizement.parse(clock, drawn, info);
  }
}
