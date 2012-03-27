package zephyr.plugin.core.privates.clocks;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.privates.ZephyrPluginCore;

public class Control implements Listener<Clock> {
  public Signal<Control> onModeChange = new Signal<Control>();
  private final Map<Clock, Integer> suspended = new LinkedHashMap<Clock, Integer>();

  public Control() {
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
    assert hasOneClockRunning();
    for (Clock clock : ZephyrPluginCore.clocks().getClocks())
      suspendClock(clock);
    onModeChange.fire(this);
  }

  public void suspend(Clock clock) {
    suspendClock(clock);
    onModeChange.fire(this);
  }


  void suspendClock(Clock clock) {
    if (!clock.info().isSuspendable)
      return;
    Integer previous = suspended.put(clock, 0);
    if (previous == null)
      clock.onTick.connect(this);
    onModeChange.fire(Control.this);
  }

  public void resume() {
    assert hasOneClockSuspended();
    for (Clock clock : getToWakeUp())
      resume(clock);
  }

  public void resume(Clock clock) {
    clock.onTick.disconnect(this);
    suspended.remove(clock);
    notifyClock(clock);
    onModeChange.fire(this);
  }

  private List<Clock> getToWakeUp() {
    return new ArrayList<Clock>(suspended.keySet());
  }

  private void notifyAll(List<Clock> toWakeUp) {
    for (Clock clock : toWakeUp)
      notifyClock(clock);
  }

  protected void notifyClock(Clock clock) {
    synchronized (clock) {
      clock.notifyAll();
    }
  }

  private boolean isSuspendedValue(Integer authorizedStep) {
    return authorizedStep != null && authorizedStep == 0;
  }

  public boolean hasOneClockRunning() {
    if (suspended.isEmpty())
      return true;
    List<Integer> authorizedSteps = new ArrayList<Integer>(suspended.values());
    for (Integer authorizedStep : authorizedSteps)
      if (!isSuspendedValue(authorizedStep))
        return true;
    return false;
  }

  public boolean hasOneClockSuspended() {
    List<Integer> authorizedSteps = new ArrayList<Integer>(suspended.values());
    for (Integer authorizedStep : authorizedSteps)
      if (isSuspendedValue(authorizedStep))
        return true;
    return false;
  }

  public boolean isSuspended(Clock clock) {
    Integer authorizedSteps = suspended.get(clock);
    return authorizedSteps != null && authorizedSteps == 0;
  }

  @Override
  public void listen(Clock clock) {
    synchronized (clock) {
      while (!clock.isTerminated() && suspended.get(clock) == 0)
        try {
          clock.wait();
          if (!suspended.containsKey(clock)) {
            onModeChange.fire(this);
            return;
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      suspended.put(clock, suspended.get(clock) - 1);
    }
    onModeChange.fire(this);
  }
}
