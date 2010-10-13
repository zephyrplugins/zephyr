package zephyr.plugin.core.api.monitoring.abstracts;

import zephyr.plugin.core.api.monitoring.LabelBuilder;

public interface DataMonitor {
  void add(Object toAdd);

  void add(String label, Monitored logged, int level);

  LabelBuilder labelBuilder();
}