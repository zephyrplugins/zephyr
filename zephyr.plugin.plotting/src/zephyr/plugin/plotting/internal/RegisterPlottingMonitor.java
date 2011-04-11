package zephyr.plugin.plotting.internal;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.Zephyr.DataMonitorProvider;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.startup.StartupJob;

public class RegisterPlottingMonitor implements StartupJob {
  @Override
  public int level() {
    return 0;
  }

  @Override
  public void run() {
    Zephyr.setDefaultMonitorProvider(new DataMonitorProvider() {
      @Override
      public DataMonitor createDataMonitor(Clock clock) {
        return ZephyrPlotting.createMonitor(clock);
      }
    });
  }
}
