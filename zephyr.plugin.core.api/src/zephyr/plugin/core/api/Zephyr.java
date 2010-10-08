package zephyr.plugin.core.api;

import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class Zephyr {
  static public class Advertized {
    public final Object advertized;
    public final Object info;
    public final Clock clock;

    public Advertized(Clock clock, Object advertized, Object info) {
      this.clock = clock;
      this.advertized = advertized;
      this.info = info;
    }
  };

  public static final Signal<Advertized> onAdvertize = new Signal<Advertized>();

  static public void advertize(Clock clock, Object drawn) {
    onAdvertize.fire(new Advertized(clock, drawn, null));
  }

  static public void advertize(Clock clock, Object info, Object drawn) {
    onAdvertize.fire(new Advertized(clock, drawn, info));
  }
}
