package zephyr.plugin.core.api.monitoring.wrappers;

import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public interface MonitorWrapper {
  Monitored createLogged(Monitored logged);
}
