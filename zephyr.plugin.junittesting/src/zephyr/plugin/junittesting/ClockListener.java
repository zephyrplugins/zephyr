package zephyr.plugin.junittesting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import zephyr.ZephyrCore;
import zephyr.ZephyrSync;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.junittesting.conditions.Condition;

class ClockListener {
  class ClockDeclared implements Listener<Clock> {
    @Override
    public void listen(Clock clock) {
      ZephyrSync.onClockAdded().disconnect(this);
      ZephyrSync.onClockRemoved().connect(new ClockRemoved());
      ClockListener.this.clock = clock;
      clock.onTick.connect(onTickListener);
    }
  }

  class ClockTick implements Listener<Clock> {
    @Override
    public void listen(Clock thisClock) {
      for (Condition condition : conditions)
        condition.listen(clock);
      if (areConditionsSatisfied()) {
        thisClock.onTick.disconnect(this);
        thisClock.terminate();
        ZephyrCore.removeClock(clock);
        onConditionSatisfied.release();
      }
    }

    private boolean areConditionsSatisfied() {
      for (Condition condition : conditions)
        if (!condition.isSatisfied())
          return false;
      return true;
    }
  }

  class ClockRemoved implements Listener<Clock> {
    @Override
    public void listen(Clock clock) {
      if (clock != ClockListener.this.clock)
        return;
      ZephyrSync.onClockRemoved().disconnect(this);
      onClockRemoved.release();
    }
  }

  final Semaphore onConditionSatisfied = new Semaphore(0);
  final Semaphore onClockRemoved = new Semaphore(0);
  final Listener<Clock> onTickListener = new ClockTick();
  final List<Condition> conditions = new ArrayList<Condition>();
  Clock clock;

  public ClockListener() {
    ZephyrSync.onClockAdded().connect(new ClockDeclared());
  }

  public Clock clock() {
    return clock;
  }

  public void registerCondition(Condition condition) {
    conditions.add(condition);
  }

  public void waitConditions() {
    try {
      onConditionSatisfied.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void waitClockRemoved() {
    try {
      onClockRemoved.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}