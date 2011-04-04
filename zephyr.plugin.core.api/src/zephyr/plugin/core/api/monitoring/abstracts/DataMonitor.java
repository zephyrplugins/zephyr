package zephyr.plugin.core.api.monitoring.abstracts;

import zephyr.plugin.core.api.parsing.LabelBuilder;

public interface DataMonitor {
  void add(Object toAdd);

  void add(Object toAdd, int level);

  void add(String label, Monitored logged, int level);

  LabelBuilder labelBuilder();
}
