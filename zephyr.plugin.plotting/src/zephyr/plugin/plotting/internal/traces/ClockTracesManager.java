package zephyr.plugin.plotting.internal.traces;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.ZephyrSync;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorSynchronizer;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class ClockTracesManager implements MonitorSynchronizer {
  private boolean forceEnabled = false;

  public final Signal<List<Trace>> onTraceAdded = new Signal<List<Trace>>();
  public final Signal<List<Trace>> onTraceRemoved = new Signal<List<Trace>>();
  private final Map<Clock, ClockTraces> clocks = new LinkedHashMap<Clock, ClockTraces>();

  public ClockTracesManager() {
    ZephyrSync.onClockRemoved().connect(new Listener<Clock>() {
      @Override
      public void listen(Clock clock) {
        removeClock(clock);
      }
    });
  }

  @Override
  synchronized public ClockTraces getSyncMonitor(Clock clock) {
    String clockLabel = clock.info().label();
    ClockTraces clockTraces = clocks.get(clock);
    if (clockTraces != null)
      return clockTraces;
    ZephyrSync.declareClock(clock);
    clockTraces = new ClockTraces(clockLabel, clock);
    assert !clocks.containsKey(clock);
    clocks.put(clock, clockTraces);
    clockTraces.onTraceAdded.connect(new Listener<List<Trace>>() {
      @Override
      public void listen(List<Trace> traces) {
        onTraceAdded.fire(traces);
      }
    });
    clockTraces.onTraceRemoved.connect(new Listener<List<Trace>>() {
      @Override
      public void listen(List<Trace> traces) {
        onTraceRemoved.fire(traces);
      }
    });
    return clockTraces;
  }

  synchronized public void removeClock(Clock clock) {
    ClockTraces clockTraces = clocks.remove(clock);
    if (clockTraces != null)
      clockTraces.dispose();
  }

  synchronized public void setForceEnabled(boolean booleanState) {
    forceEnabled = booleanState;
    for (ClockTraces clockTraces : clocks.values())
      clockTraces.selection.setForceEnabled(booleanState);
  }

  synchronized public List<ClockTraces> getClockTraces() {
    return new ArrayList<ClockTraces>(clocks.values());
  }

  public boolean forceEnabled() {
    return forceEnabled;
  }
}
