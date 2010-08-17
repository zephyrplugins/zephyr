package zephyr.plugin.core.views;

public interface TimedView extends SyncView {
  void addTimed(String info, Object drawn);

  boolean canTimedAdded();
}
