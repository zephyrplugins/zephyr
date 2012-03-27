package zephyr.plugin.core.api.internal.codeparser.codetree;

import java.util.Arrays;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.ParentNode;

public abstract class AbstractCodeNode implements CodeNode {
  static private final String[] PathRoot = new String[] {};
  private final ParentNode parent;
  private final String label;
  private final int level;
  private String[] path = null;

  protected AbstractCodeNode(String label, ParentNode parent, int level) {
    this.parent = parent;
    this.label = label;
    this.level = parent != null ? Math.max(level, parent.level()) : level;
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
  public String[] path() {
    if (path == null) {
      String[] parentPath = parent != null ? parent.path() : PathRoot;
      path = Arrays.copyOf(parentPath, parentPath.length + 1);
      path[path.length - 1] = label;
    }
    return path;
  }
}
