package zephyr.plugin.plotting.internal.traces;

import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class Trace {
  public final ClockTraces clockTraces;
  public final String label;
  public final Monitored logged;
  public final int level;

  public Trace(ClockTraces clockTraces, String label, int level, Monitored logged) {
    this.clockTraces = clockTraces;
    this.label = label;
    this.logged = logged;
    this.level = level;
  }

  @Override
  public String toString() {
    return label;
  }
}
