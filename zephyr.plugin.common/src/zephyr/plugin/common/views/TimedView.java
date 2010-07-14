package zephyr.plugin.common.views;

public interface TimedView extends SyncView {
  void addTimed(Object drawn);

  boolean canTimedAdded();
}
