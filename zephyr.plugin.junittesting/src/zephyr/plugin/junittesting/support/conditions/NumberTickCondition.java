package zephyr.plugin.junittesting.support.conditions;

import zephyr.plugin.core.api.synchronization.Clock;


public class NumberTickCondition implements Condition {
  private final long nbMaxTick;
  private boolean satisfied = false;

  public NumberTickCondition(long nbMaxTick) {
    this.nbMaxTick = nbMaxTick;
  }

  @Override
  public void listen(Clock clock) {
    if (clock.timeStep() > nbMaxTick)
      satisfied = true;
  }

  @Override
  public boolean isSatisfied() {
    return satisfied;
  }
}
