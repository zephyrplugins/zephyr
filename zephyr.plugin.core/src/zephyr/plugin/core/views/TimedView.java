package zephyr.plugin.core.views;

import zephyr.plugin.core.api.synchronization.Clock;

public interface TimedView extends SyncView {
  boolean addTimed(Clock clock, Object drawn, Object info);

  void removeTimed(Clock clock);
}
