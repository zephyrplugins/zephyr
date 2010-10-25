package zephyr.plugin.core.views;

import zephyr.plugin.core.api.synchronization.Clock;

public interface TimedView extends SyncView {
  void addTimed(Clock clock, Object drawn, Object info);

  boolean canAddTimed();
}
