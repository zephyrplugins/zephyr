package zephyr;

import zephyr.plugin.core.api.monitoring.abstracts.Logger;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.plotting.internal.traces.ClockTracesManager;

public class ZephyrPlotting {
  static public Logger createLogger(String label, Clock clock) {
    return ClockTracesManager.manager().addClock(label, clock);
  }
}
