package zephyr.plugin.common.control;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rlpark.plugin.utils.events.Listener;
import rlpark.plugin.utils.events.Signal;
import rlpark.plugin.utils.time.Clock;
import zephyr.plugin.common.ZephyrPluginCommon;

public class Control implements Listener<Clock> {
  public Signal<Control> onModeChange = new Signal<Control>();
  protected Listener<Clock> addedClockListener = null;
  private final Map<Clock, Integer> suspended = new LinkedHashMap<Clock, Integer>();

  public Control() {
  }

  public void connectSuspendOnJobStarting() {
    addedClockListener = new Listener<Clock>() {
      @Override
      public void listen(Clock clock) {
        suspendClock(clock);
        onModeChange.fire(Control.this);
      }
    };
    ZephyrPluginCommon.viewBinder().onClockAdded.connect(addedClockListener);
  }

  public void step() {
    for (Map.Entry<Clock, Integer> entry : suspended.entrySet())
      suspended.put(entry.getKey(), 1);
    notifyAll(getToWakeUp());
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
    suspended.put(clock, 0);
    clock.onTick.connect(this);
  }

  public void resume() {
    assert isSuspended();
    disconnectOnJobStartingIFN();
    List<Clock> toWakeUp = getToWakeUp();
    for (Clock clock : toWakeUp)
      clock.onTick.disconnect(this);
    suspended.clear();
    notifyAll(toWakeUp);
    onModeChange.fire(this);
  }

  private void disconnectOnJobStartingIFN() {
    if (addedClockListener == null)
      return;
    ZephyrPluginCommon.viewBinder().onClockAdded.disconnect(addedClockListener);
    addedClockListener = null;
  }

  private List<Clock> getToWakeUp() {
    return new ArrayList<Clock>(suspended.keySet());
  }

  private void notifyAll(List<Clock> toWakeUp) {
    for (Clock clock : toWakeUp) {
      synchronized (clock) {
        clock.notifyAll();
      }
    }
  }

  public boolean isSuspended() {
    return !suspended.isEmpty();
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
