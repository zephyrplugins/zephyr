package zephyr.plugin.core.views;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;

public interface ViewProvider {
  /**
   * Ask to this provider if it can provide the ID of a view able to display an
   * object
   * 
   * @param drawn
   *          the object the view should be able to draw
   * @return true if this is able to provide the ID of a view able to display
   *         'drawn', else false
   */
  boolean canViewDraw(CodeNode codeNode);
}
