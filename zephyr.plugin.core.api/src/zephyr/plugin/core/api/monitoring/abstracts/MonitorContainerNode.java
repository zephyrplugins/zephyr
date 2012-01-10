package zephyr.plugin.core.api.monitoring.abstracts;

public interface MonitorContainerNode {
  Monitored createMonitored(String label);

  String[] createLabels();

  Monitored[] createMonitored();
}
