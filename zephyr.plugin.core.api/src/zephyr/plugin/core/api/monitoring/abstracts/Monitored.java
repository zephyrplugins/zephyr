package zephyr.plugin.core.api.monitoring.abstracts;

public interface Monitored {
  double monitoredValue(long stepTime);
}
