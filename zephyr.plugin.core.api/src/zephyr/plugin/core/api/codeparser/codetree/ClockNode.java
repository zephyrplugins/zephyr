package zephyr.plugin.core.api.codeparser.codetree;

import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.synchronization.Clock;

public class ClockNode extends AbstractParentNode {
  private final Clock clock;

  public ClockNode(Clock clock) {
    super(clock.info().label(), null, 0);
    this.clock = clock;
  }

  public Clock clock() {
    return clock;
  }

  @Override
  public ParentNode parent() {
    return null;
  }
}
