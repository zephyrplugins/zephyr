package zephyr.plugin.core.control;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.SavedSettings;
import zephyr.plugin.core.internal.ZephyrPluginCommon;
import zephyr.plugin.core.utils.Helper;

public class Control implements Listener<Clock> {
  public Signal<Control> onModeChange = new Signal<Control>();
  private final Map<Clock, Integer> suspended = new LinkedHashMap<Clock, Integer>();

  public Control() {
  }

  public void connectSuspendOnJobStarting() {
    ZephyrPluginCommon.viewBinder().onClockAdded.connect(new Listener<Clock>() {
      @Override
      public void listen(Clock clock) {
        if (!Helper.booleanState(SavedSettings.STARTSUSPENDED, true))
          return;
        suspendClock(clock);
        onModeChange.fire(Control.this);
      }
    });
  }

  public void step(int nbTimeSteps) {
    for (Map.Entry<Clock, Integer> entry : suspended.entrySet())
      suspended.put(entry.getKey(), nbTimeSteps);
    onModeChange.fire(this);
    notifyAll(getToWakeUp());
  }

  public void step() {
    step(1);
  }

  public void suspend() {
    assert !isSuspended();
    for (Clock clock : ZephyrPluginCommon.viewBinder().getClocks())
      suspendClock(clock);
    onModeChange.fire(this);
  }

  protected void suspendClock(Clock clock) {
    if (!clock.isSuspendable)
      return;
    Integer previous = suspended.put(clock, 0);
    if (previous == null)
      clock.onTick.connect(this);
  }

  public void resume() {
    assert isSuspended();
    List<Clock> toWakeUp = getToWakeUp();
    for (Clock clock : toWakeUp)
      clock.onTick.disconnect(this);
    suspended.clear();
    notifyAll(toWakeUp);
    onModeChange.fire(this);
  }

  private List<Clock> getToWakeUp() {
    return new ArrayList<Clock>(suspended.keySet());
  }

  private void notifyAll(List<Clock> toWakeUp) {
    for (Clock clock : toWakeUp)
      synchronized (clock) {
        clock.notifyAll();
      }
  }

  public boolean isSuspended() {
    List<Integer> remainingSteps = new ArrayList<Integer>(suspended.values());
    for (Integer remainingStep : remainingSteps)
      if (remainingStep != 0)
        return false;
    return !remainingSteps.isEmpty();
  }

  public boolean isSuspended(Clock clock) {
    Integer authorizedSteps = suspended.get(clock);
    return authorizedSteps != null && authorizedSteps == 0;
  }

  @Override
  public void listen(Clock clock) {
    synchronized (clock) {
      while (suspended.get(clock) == 0)
        try {
          clock.wait();
          if (!suspended.containsKey(clock))
            return;
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      suspended.put(clock, suspended.get(clock) - 1);
    }
  }
}
