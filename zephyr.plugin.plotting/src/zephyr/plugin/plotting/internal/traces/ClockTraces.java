package zephyr.plugin.plotting.internal.traces;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;

public class ClockTraces {
  public final Clock clock;
  private final Map<Trace, TraceData> traces = new LinkedHashMap<Trace, TraceData>();
  private final Listener<Clock> onTickClockListener = new Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      update(clock);
    }
  };

  protected ClockTraces(Clock clock) {
    this.clock = clock;
    clock.onTick.connect(onTickClockListener);
  }

  synchronized protected void update(Clock clock) {
    assert clock == this.clock;
    for (TraceData traceData : traces.values())
      traceData.update(clock.timeStep());
  }

  synchronized public void dispose() {
    traces.clear();
  }

  public TraceData traceData(Trace trace) {
    return traceData(trace, null);
  }

  synchronized public TraceData traceData(Trace trace, Monitored monitored) {
    TraceData traceData = traces.get(trace);
    Trace lightTrace = new Trace(trace.label, trace.codeNode);
    if (traceData == null) {
      Monitored monitoredFound = findMonitored(trace, monitored);
      if (monitoredFound == null)
        return null;
      traceData = new TraceData(lightTrace, monitoredFound);
      traces.put(new Trace(trace.label, trace.codeNode), traceData);
    }
    return traceData;
  }

  private Monitored findMonitored(Trace trace, Monitored monitored) {
    Monitored result = monitored;
    if (result != null)
      return result;
    if (trace instanceof TraceExtended)
      result = ((TraceExtended) trace).monitored;
    if (result != null)
      return result;
    return ((MonitorContainerNode) trace.codeNode).createMonitored(trace.label);
  }

  public void gc() {
    List<TraceData> datas = new ArrayList<TraceData>(traces.values());
    for (TraceData traceData : datas)
      if (traceData.ref() == 0)
        traces.remove(traceData.trace);
  }
}
