package zephyr.plugin.core.api.internal.monitoring.abstracts;

import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public interface MonitorContainerNode {
  Monitored createMonitored(String label);

  String[] createLabels();

  Monitored[] createMonitored();
}
