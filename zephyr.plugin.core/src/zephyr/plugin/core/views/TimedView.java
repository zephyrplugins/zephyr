package zephyr.plugin.core.views;

public interface TimedView extends SyncView {
  void addTimed(Object drawn, Object info);

  boolean canTimedAdded();
}
