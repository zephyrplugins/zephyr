package zephyr.plugin.core.api.logging.abstracts;

import zephyr.plugin.core.api.logging.LabelBuilder;
import zephyr.plugin.core.api.synchronization.Clock;

public interface Logger {
  void add(Object toAdd);

  void add(String label, Monitored logged);

  LabelBuilder labelBuilder();

  Logger newClock(String label, Clock clock);
}
