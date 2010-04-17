package zephyr;

import rlpark.plugin.utils.logger.abstracts.Logger;
import rlpark.plugin.utils.time.Clock;
import zephyr.plugin.plotting.ZephyrPluginPlotting;

public class ZephyrPlotting {
  static public Logger createLogger(String label, Clock clock) {
    return ZephyrPluginPlotting.clockTracesManager().addClock(label, clock);
  }
}
