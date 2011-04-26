package zephyr.plugin.plotting.internal.traces;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.synchronization.Clock;

public class TraceManager implements DataMonitor {
  private final Clock clock;
  private final Map<String, TraceData> traces = new HashMap<String, TraceData>();

  public TraceManager(Clock clock) {
    this.clock = clock;
  }

  @Override
  public void add(String label, Monitored monitored) {
    Trace trace = new Trace(clock, label, monitored);
    TraceData traceData = new TraceData(trace);
    traces.put(label, traceData);
  }

  public Collection<String> labels() {
    return traces.keySet();
  }
}
