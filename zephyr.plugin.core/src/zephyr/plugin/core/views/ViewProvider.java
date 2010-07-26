package zephyr.plugin.core.views;

public interface ViewProvider {
  String viewID();

  boolean canViewDraw(Object drawn);
}
