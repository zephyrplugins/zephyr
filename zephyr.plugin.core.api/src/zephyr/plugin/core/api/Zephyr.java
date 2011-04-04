package zephyr.plugin.core.api;

import zephyr.plugin.core.api.advertisement.Advertisement;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.parsing.LabelBuilder;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Timed;

public class Zephyr {
  static public interface DataMonitorProvider {
    DataMonitor createDataMonitor(Clock clock);
  }

  static private Advertisement advertisement = new Advertisement();
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
        public void add(String label, Monitored logged, int level) {
        }

        @Override
        public void add(Object toAdd, int level) {
        }

        @Override
        public void add(Object toAdd) {
        }
      };
    }
  };

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

  public static DataMonitor createMonitor(Clock clock) {
    return dataMonitorProvider.createDataMonitor(clock);
  }

  public static void setDefaultMonitorProvider(DataMonitorProvider dataMonitorProvider) {
    Zephyr.dataMonitorProvider = dataMonitorProvider;
  }
}
