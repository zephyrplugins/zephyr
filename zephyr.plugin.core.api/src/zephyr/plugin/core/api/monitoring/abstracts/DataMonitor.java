package zephyr.plugin.core.api.monitoring.abstracts;

public interface DataMonitor {
  void add(String label, int level, Monitored monitored);
}
