package zephyr.plugin.common.helpers;

import zephyr.plugin.common.views.ViewProvider;

public class ClassViewProvider implements ViewProvider {

  private final String viewID;
  private final Class<?> drawnClass;

  public ClassViewProvider(Class<?> drawnClass, String viewID) {
    this.viewID = viewID;
    this.drawnClass = drawnClass;
  }

  @Override
  public String viewID() {
    return viewID;
  }

  @Override
  public boolean canViewDraw(Object drawn) {
    return drawnClass.isInstance(drawn);
  }
}
