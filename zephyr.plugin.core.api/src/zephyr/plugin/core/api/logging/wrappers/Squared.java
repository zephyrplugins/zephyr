package zephyr.plugin.core.api.logging.wrappers;

import zephyr.plugin.core.api.logging.abstracts.Monitored;

public class Squared implements MonitorWrapper {
  public static final String ID = "squared";

  @Override
  public Monitored createLogged(final Monitored logged) {
    return new Monitored() {
      @Override
      public double loggedValue(long stepTime) {
        double value = logged.loggedValue(stepTime);
        return value * value;
      }
    };
  }
}
