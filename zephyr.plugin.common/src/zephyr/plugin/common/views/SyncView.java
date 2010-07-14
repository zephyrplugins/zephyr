package zephyr.plugin.common.views;

public interface SyncView {
  boolean synchronize();

  void repaint();

  boolean isDisposed();
}
