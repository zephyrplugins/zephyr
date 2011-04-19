package zephyr.plugin.core.helpers;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.views.ViewProvider;

public class ClassViewProvider implements ViewProvider {
  protected final Class<?> drawnClass;

  public ClassViewProvider(Class<?> drawnClass) {
    this.drawnClass = drawnClass;
  }

  @Override
  public boolean canViewDraw(CodeNode codeNode) {
    if (!(codeNode instanceof ClassNode))
      return false;
    return drawnClass.isInstance(((ClassNode) codeNode).instance());
  }
}
