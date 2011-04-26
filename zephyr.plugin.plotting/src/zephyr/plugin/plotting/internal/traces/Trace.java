package zephyr.plugin.plotting.internal.traces;

import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.synchronization.Clock;

public class Trace {
  public final String label;
  public final Monitored logged;
  public final Clock clock;

  public Trace(Clock clock, String label, Monitored logged) {
    this.label = label;
    this.logged = logged;
    this.clock = clock;
  }

  @Override
  public String toString() {
    return label;
  }
}
