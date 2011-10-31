package zephyr.plugin.junittesting.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import junit.framework.Assert;
import zephyr.ZephyrCore;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.async.events.CastedEventListener;
import zephyr.plugin.core.events.ClockEvent;
import zephyr.plugin.junittesting.support.conditions.Condition;

public class ClockListener {
  class ClockAdded extends CastedEventListener<ClockEvent> {
    @Override
    public void listenEvent(ClockEvent event) {
      print(event.clock().info().label() + " added");
      ZephyrCore.busEvent().unregister(ClockEvent.AddedID, this);
      Assert.assertTrue(clock == null);
      clock = event.clock();
      ZephyrCore.busEvent().register(ClockEvent.RemovedID, new ClockRemoved());
      clock.onTick.connect(onTickListener);
    }
  }

  class ClockTick implements Listener<Clock> {
    @Override
    public void listen(Clock thisClock) {
      for (Condition condition : conditions)
        condition.listen(clock);
      if (areConditionsSatisfied()) {
        print(clock.info().label() + " condition satisfied");
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

  class ClockRemoved extends CastedEventListener<ClockEvent> {
    @Override
    public void listenEvent(ClockEvent event) {
      if (event.clock() != clock)
        return;
      print(clock.info().label() + " removed");
      ZephyrCore.busEvent().unregister(ClockEvent.RemovedID, new ClockRemoved());
      onClockRemoved.release();
    }
  }

  static private boolean verbose = false;
  final Semaphore onConditionSatisfied = new Semaphore(0);
  final Semaphore onClockRemoved = new Semaphore(0);
  final Listener<Clock> onTickListener = new ClockTick();
  final List<Condition> conditions = new ArrayList<Condition>();
  Clock clock;

  public ClockListener() {
    ZephyrCore.busEvent().register(ClockEvent.AddedID, new ClockAdded());
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

  static public void enableVerbose() {
    verbose = true;
  }

  static void print(String message) {
    if (verbose)
      System.out.println(message);
  }
}