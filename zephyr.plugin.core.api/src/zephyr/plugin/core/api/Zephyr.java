package zephyr.plugin.core.api;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.parsing.LabelBuilder;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class Zephyr {
  static public interface DataMonitorProvider {
    DataMonitor createDataMonitor(Clock clock);
  }

  static public class AdvertisementInfo {
    public final Clock clock;
    public final Object advertised;

    AdvertisementInfo(Clock clock, Object advertised) {
      this.clock = clock;
      this.advertised = advertised;
    }
  }

  static public final Signal<AdvertisementInfo> onAdvertised = new Signal<AdvertisementInfo>();

  static private DataMonitorProvider dataMonitorProvider = new DataMonitorProvider() {
    @Override
    public DataMonitor createDataMonitor(Clock clock) {
      return new DataMonitor() {
        LabelBuilder labelBuilder = new LabelBuilder();

        @Override
        public LabelBuilder labelBuilder() {
          return labelBuilder;
        }

        @Override
        public void add(String label, Monitored logged) {
        }

        @Override
        public void add(Object toAdd, int level) {
          System.err.println("Zephyr warning: using default data monitor");
        }

        @Override
        public void add(Object toAdd) {
          add(toAdd, 0);
        }
      };
    }
  };

  static public void advertise(Clock clock, Object advertised) {
    onAdvertised.fire(new AdvertisementInfo(clock, advertised));
    createMonitor(clock).add(advertised);
  }

  private static DataMonitor createMonitor(Clock clock) {
    return dataMonitorProvider.createDataMonitor(clock);
  }

  public static void setDefaultMonitorProvider(DataMonitorProvider dataMonitorProvider) {
    Zephyr.dataMonitorProvider = dataMonitorProvider;
  }
}
