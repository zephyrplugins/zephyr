package zephyr.plugin.core.api.codeparser.codetree;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.synchronization.Clock;

public abstract class AbstractCodeNode implements CodeNode {
  private final ParentNode parent;
  private final String label;
  private String path = null;

  protected AbstractCodeNode(String label, ParentNode parent) {
    this.parent = parent;
    this.label = label;
  }

  @Override
  public ClockNode root() {
    return parent.root();
  }

  @Override
  public ParentNode parent() {
    return parent;
  }

  @Override
  public Clock clock() {
    return parent.clock();
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
