package zephyr.plugin.core.api.internal.monitoring.abstracts;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.synchronization.Clock;

public interface MonitorSynchronizer {
  DataMonitor getSyncMonitor(Clock clock);
}
