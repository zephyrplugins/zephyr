package zephyr.plugin.core.views;

import zephyr.plugin.core.api.synchronization.Clock;

public interface SyncView {
  boolean synchronize(Clock clock);

  void repaint();
}
