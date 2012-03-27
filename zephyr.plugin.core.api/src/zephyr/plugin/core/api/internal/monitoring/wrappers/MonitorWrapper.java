package zephyr.plugin.core.api.internal.monitoring.wrappers;

import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public interface MonitorWrapper {
  Monitored createMonitored(Monitored logged);
}
