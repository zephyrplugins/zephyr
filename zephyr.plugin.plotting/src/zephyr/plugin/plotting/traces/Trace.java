package zephyr.plugin.plotting.traces;

import rlpark.plugin.utils.logger.abstracts.Logged;

public class Trace {
  public final ClockTraces clockTraces;
  public final String label;
  public final Logged logged;

  public Trace(ClockTraces clockTraces, String label, Logged logged) {
    this.clockTraces = clockTraces;
    this.label = label;
    this.logged = logged;
  }

  @Override
  public String toString() {
    return label;
  }
}
