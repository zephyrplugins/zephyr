package zephyr.plugin.tests.codeparser;

import zephyr.plugin.core.api.synchronization.Clock;

public class ClockNode extends AbstractCodeNode {
  private final Clock clock;

  public ClockNode(Clock clock) {
    super(null);
    this.clock = clock;
  }

  @Override
  public String label() {
    return clock.info().label();
  }

  public ClassNode addClassNode(Object root) {
    ClassNode rootClassNode = new ClassNode(this, root);
    addChild(rootClassNode);
    return rootClassNode;
  }

  @Override
  public Clock clock() {
    return clock;
  }

  @Override
  public ClockNode root() {
    return this;
  }
}
