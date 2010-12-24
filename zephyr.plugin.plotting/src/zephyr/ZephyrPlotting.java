package zephyr;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.plotting.internal.traces.ClockTracesManager;

public class ZephyrPlotting {
  static public DataMonitor createLogger(Clock clock) {
    return ClockTracesManager.manager().addClock(clock.info().label(), clock);
  }

  /**
   * Specify the label through Clock constructor
   */
  @Deprecated
  static public DataMonitor createLogger(String label, Clock clock) {
    return ClockTracesManager.manager().addClock(label, clock);
  }
}
