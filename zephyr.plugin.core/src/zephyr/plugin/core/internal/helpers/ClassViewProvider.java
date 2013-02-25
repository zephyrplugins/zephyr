package zephyr.plugin.core.internal.helpers;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.internal.views.ViewProvider;

public class ClassViewProvider implements ViewProvider {
  protected final Class<?>[] drawnClasses;

  public ClassViewProvider(Class<?>... drawnClasses) {
    this.drawnClasses = drawnClasses;
  }

  @Override
  public boolean canViewDraw(CodeNode codeNode) {
    if (!(codeNode instanceof ClassNode))
      return false;
    Object instance = ((ClassNode) codeNode).instance();
    return isInstanceSupported(instance);
  }

  protected boolean isInstanceSupported(Object instance) {
    for (Class<?> drawnClass : drawnClasses)
      if (drawnClass.isInstance(instance))
        return true;
    return false;
  }
}
