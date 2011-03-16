package zephyr.plugin.core.api.monitoring.wrappers;

import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class Abs implements MonitorWrapper {
  public static final String ID = "abs";

  @Override
  public Monitored createMonitored(final Monitored logged) {
    return new Monitored() {
      @Override
      public double monitoredValue(long stepTime) {
        return Math.abs(logged.monitoredValue(stepTime));
      }
    };
  }
}
