package zephyr.plugin.plotting.internal.traces;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.api.monitoring.abstracts.Logger;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.ClockKillable;

public class ClockTracesManager {
  static final int TraceTime = 300; // Seconds
  static protected boolean forceEnabled = false;
  private static ClockTracesManager manager = null;

  public final Signal<List<Trace>> onTraceAdded = new Signal<List<Trace>>();
  public final Signal<List<Trace>> onTraceRemoved = new Signal<List<Trace>>();
  private final Map<Clock, ClockTraces> clocks = new LinkedHashMap<Clock, ClockTraces>();
  private final Listener<Clock> onKillClockListener = new Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      removeClock(clock);
    }
  };

  public ClockTracesManager() {
  }

  synchronized public Logger addClock(String clockLabel, Clock clock) {
    ClockTraces clockTraces = clocks.get(clock);
    if (clockTraces != null)
      return clockTraces;
    clockTraces = new ClockTraces(clockLabel, clock);
    clocks.put(clock, clockTraces);
    if (clock instanceof ClockKillable)
      ((ClockKillable) clock).onKill.connect(onKillClockListener);
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
    if (clock instanceof ClockKillable)
      ((ClockKillable) clock).onKill.disconnect(onKillClockListener);
    ClockTraces clockTraces = clocks.remove(clock);
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

  static public ClockTracesManager manager() {
    return manager;
  }

  static public void setManager(ClockTracesManager manager) {
    ClockTracesManager.manager = manager;
  }
}
