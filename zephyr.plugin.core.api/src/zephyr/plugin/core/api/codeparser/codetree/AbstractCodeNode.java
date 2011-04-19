package zephyr.plugin.core.api.codeparser.codetree;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;

public abstract class AbstractCodeNode implements CodeNode {
  private final ParentNode parent;
  private final String label;
  private String path = null;
  private final int level;

  protected AbstractCodeNode(String label, ParentNode parent, int level) {
    this.parent = parent;
    this.label = label;
    this.level = level;
  }

  @Override
  public int level() {
    return level;
  }

  @Override
  public ParentNode parent() {
    return parent;
  }

  @Override
  public String label() {
    return label;
  }

  @Override
  public String path() {
    if (path == null) {
      String parentPath = "";
      if (parent != null) {
        parentPath = parent.path();
        if (!(parent instanceof ObjectCollectionNode) && !parentPath.isEmpty() && !label().isEmpty())
          parentPath += "/";
      }
      path = parentPath + label;
    }
    return path;
  }
}
