package zephyr.plugin.common.views;


public interface TimedView extends SyncView {
  String viewID();

  boolean canDraw(Object drawn);

  void setTimed(Object drawn);

  Object drawn();
}
