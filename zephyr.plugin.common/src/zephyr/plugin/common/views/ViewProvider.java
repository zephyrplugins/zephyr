package zephyr.plugin.common.views;

public interface ViewProvider {
  String viewID();

  boolean canViewDraw(Object drawn);
}
