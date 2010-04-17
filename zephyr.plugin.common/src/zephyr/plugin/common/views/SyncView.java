package zephyr.plugin.common.views;

import rlpark.plugin.utils.events.Signal;

public interface SyncView {
  void synchronize();

  void repaint();

  Signal<SyncView> onPaintDone();

  Signal<SyncView> onDispose();
}
