package zephyr.plugin.tests.codeparser;

import java.util.ArrayList;
import java.util.List;

import zephyr.plugin.core.api.synchronization.Clock;

public abstract class AbstractCodeNode implements CodeNode {
  private final List<CodeNode> children = new ArrayList<CodeNode>();
  private final CodeNode parent;

  protected AbstractCodeNode(CodeNode parent) {
    this.parent = parent;
  }

  @Override
  public ClockNode root() {
    return parent.root();
  }

  protected void addChild(CodeNode child) {
    children.add(child);
  }

  @Override
  public List<CodeNode> children() {
    return children;
  }

  @Override
  public CodeNode parent() {
    return parent;
  }

  @Override
  public Clock clock() {
    return parent.clock();
  }

}
