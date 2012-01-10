package zephyr.plugin.core.api.monitoring.abstracts;

public interface DataMonitor {
  void add(String label, Monitored monitored);
}
