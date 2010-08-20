package zephyr.plugin.core.api.logging.wrappers;

import zephyr.plugin.core.api.logging.abstracts.Monitored;

public interface MonitorWrapper {
  Monitored createLogged(Monitored logged);
}
