package zephyr.plugin.core.api.codeparser.codetree;

import java.util.Arrays;

import zephyr.plugin.core.api.synchronization.Clock;

public abstract class AbstractCodeNode implements CodeNode {
  private final ParentNode parent;
  private final String label;
  private String[] path;

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
  public String id() {
    return label;
  }

  @Override
  public String label() {
    return label;
  }

  @Override
  public String[] path() {
    if (path == null) {
      String[] parentPath = parent != null ? parent.path() : new String[] {};
      path = Arrays.copyOf(parentPath, parentPath.length + 1);
      path[path.length - 1] = label;
    }
    return path;
  }
}
