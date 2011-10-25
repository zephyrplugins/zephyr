package zephyr.plugin.junittesting.support.conditions;

import zephyr.plugin.core.api.synchronization.Clock;

public class FalseCondition implements Condition {
  @Override
  public void listen(Clock eventInfo) {
  }

  @Override
  public boolean isSatisfied() {
    return false;
  }
}
