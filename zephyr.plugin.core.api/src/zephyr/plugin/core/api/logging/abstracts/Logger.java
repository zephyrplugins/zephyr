package zephyr.plugin.core.api.logging.abstracts;

import zephyr.plugin.core.api.logging.LabelBuilder;

public interface Logger {
  void add(Object toAdd);

  void add(String label, Monitored logged);

  LabelBuilder labelBuilder();
}
