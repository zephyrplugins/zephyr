package zephyr.plugin.core.api.logging.wrappers;

import zephyr.plugin.core.api.logging.abstracts.Monitored;

public class Abs implements MonitorWrapper {
  public static final String ID = "abs";

  @Override
  public Monitored createLogged(final Monitored logged) {
    return new Monitored() {
      @Override
      public double loggedValue(long stepTime) {
        return Math.abs(logged.loggedValue(stepTime));
      }
    };
  }
}
