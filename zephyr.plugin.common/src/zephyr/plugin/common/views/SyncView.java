package zephyr.plugin.common.views;

import rlpark.plugin.utils.events.Signal;

public interface SyncView {
  boolean synchronize();

  void repaint();

  Signal<SyncView> onDispose();
}
