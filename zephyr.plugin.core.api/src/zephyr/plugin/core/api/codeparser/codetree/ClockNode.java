package zephyr.plugin.core.api.codeparser.codetree;

import zephyr.plugin.core.api.synchronization.Clock;

public class ClockNode extends AbstractParentNode {
  private final Clock clock;

  public ClockNode(Clock clock) {
    super(clock.info().label(), null, null);
    this.clock = clock;
  }

  @Override
  public Clock clock() {
    return clock;
  }

  @Override
  public ClockNode root() {
    return this;
  }

  @Override
  public ParentNode parent() {
    return null;
  }

  @Override
  public ClassNode getChild(int index) {
    return (ClassNode) super.getChild(index);
  }
}
