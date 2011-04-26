package zephyr.plugin.plotting.internal.traces;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import zephyr.plugin.core.api.synchronization.Clock;

public class ClockTracesManager {
  private final Map<Clock, TraceManager> clocks = new LinkedHashMap<Clock, TraceManager>();

  public TraceManager traceManager(Clock clock) {
    TraceManager clockToTraces = clocks.get(clock);
    if (clockToTraces == null) {
      clockToTraces = new TraceManager(clock);
      clocks.put(clock, clockToTraces);
    }
    return clockToTraces;
  }

  public Collection<TraceManager> traceManagers() {
    return clocks.values();
  }
}
