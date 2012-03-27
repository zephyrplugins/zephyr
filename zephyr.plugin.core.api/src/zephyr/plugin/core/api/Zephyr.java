package zephyr.plugin.core.api;

import java.util.ArrayList;
import java.util.List;

import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitorRegistry;
import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitorSynchronizer;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class Zephyr {
  static public class AdvertisementInfo {
    public final Clock clock;
    public final Object advertised;
    public final String label;

    AdvertisementInfo(Clock clock, Object advertised, String label) {
      this.clock = clock;
      this.advertised = advertised;
      this.label = label;
    }
  }

  static public final Signal<AdvertisementInfo> onAdvertised = new Signal<AdvertisementInfo>();

  static public void advertise(Clock clock, Object advertised) {
    advertise(clock, advertised, null);
  }

  static public void advertise(Clock clock, Object advertised, String label) {
    onAdvertised.fire(new AdvertisementInfo(clock, advertised, label));
  }

  static public DataMonitor getSynchronizedMonitor(Clock clock) {
    List<DataMonitor> monitors = new ArrayList<DataMonitor>();
    for (MonitorSynchronizer factory : MonitorRegistry.factories)
      monitors.add(factory.getSyncMonitor(clock));
    return new MonitorRegistry.MonitorCollection(monitors);
  }
}
