package zephyr.plugin.core.api.monitoring.abstracts;


public interface MonitorContainer {
  void addToMonitor(MonitorParser parser, DataMonitor monitor);
}
