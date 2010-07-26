package zephyr.plugin.core.views;

public interface TimedView extends SyncView {
  void addTimed(Object drawn);

  boolean canTimedAdded();
}
