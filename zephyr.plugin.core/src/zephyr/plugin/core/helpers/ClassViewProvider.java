package zephyr.plugin.core.helpers;

import zephyr.plugin.core.views.ViewProvider;

public class ClassViewProvider implements ViewProvider {
  protected final Class<?> drawnClass;

  public ClassViewProvider(Class<?> drawnClass) {
    this.drawnClass = drawnClass;
  }

  @Override
  public boolean canViewDraw(Object drawn) {
    return drawnClass.isInstance(drawn);
  }

  @Override
  public boolean allowNewView() {
    return true;
  }
}
