package zephyr.plugin.core.api.internal.monitoring.wrappers;

import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class Squared implements MonitorWrapper {
  public static final String ID = "squared";

  @Override
  public Monitored createMonitored(final Monitored logged) {
    return new Monitored() {
      @Override
      public double monitoredValue() {
        double value = logged.monitoredValue();
        return value * value;
      }
    };
  }
}
