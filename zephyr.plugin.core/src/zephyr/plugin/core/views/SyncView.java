package zephyr.plugin.core.views;

public interface SyncView {
  boolean synchronize();

  void repaint();

  boolean isDisposed();
}
