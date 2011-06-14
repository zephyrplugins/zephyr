package zephyr.plugin.plotting.internal.traces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;

public class TracesSelection {
  public static interface TraceSelector {
  }

  private final Listener<List<Trace>> traceAddedListener = new Listener<List<Trace>>() {
    @Override
    public void listen(List<Trace> traces) {
      addTraces(traces);
    }
  };
  private final Listener<List<Trace>> traceRemovedListener = new Listener<List<Trace>>() {
    @Override
    public void listen(List<Trace> traces) {
      removeTraces(null, traces);
    }
  };

  private final Map<Trace, TraceData> enabledTrace = new ConcurrentHashMap<Trace, TraceData>();
  private final ClockTraces clockTraces;
  private boolean forceEnabled;

  public TracesSelection(ClockTraces clockTraces) {
    this.clockTraces = clockTraces;
    clockTraces.onTraceAdded.connect(traceAddedListener);
    clockTraces.onTraceRemoved.connect(traceRemovedListener);
    setForceEnabled(ZephyrPluginPlotting.tracesManager().forceEnabled());
  }

  private TraceData enableTrace(Trace trace) {
    TraceData traceData = enabledTrace.get(trace);
    if (traceData != null)
      return traceData;
    traceData = new TraceData(trace);
    enabledTrace.put(trace, traceData);
    return traceData;
  }

  private void purgeIFN() {
    if (forceEnabled)
      return;
    for (Map.Entry<Trace, TraceData> entry : enabledEntries()) {
      TraceData traceData = entry.getValue();
      Trace trace = entry.getKey();
      if (!traceData.isSelected())
        removeTrace(null, trace);
    }
  }

  private Set<Entry<Trace, TraceData>> enabledEntries() {
    return new HashSet<Map.Entry<Trace, TraceData>>(enabledTrace.entrySet());
  }

  synchronized public void update(Clock clock) {
    for (TraceData traceData : enabledTrace.values())
      traceData.update(clock.timeStep());
  }

  synchronized public void setForceEnabled(boolean forceEnabled) {
    this.forceEnabled = forceEnabled;
    if (!forceEnabled) {
      purgeIFN();
      return;
    }
    autoEnableTraces(clockTraces.getTraces());
  }

  synchronized protected void addTraces(List<Trace> traces) {
    if (!forceEnabled)
      return;
    autoEnableTraces(traces);
  }

  private void autoEnableTraces(List<Trace> traces) {
    for (Trace trace : traces)
      if (trace.level <= 1)
        enableTrace(trace);
  }

  synchronized protected void removeTraces(TraceSelector selector, Collection<Trace> traces) {
    for (Trace trace : traces)
      removeTrace(selector, trace);
  }

  private void removeTrace(TraceSelector selector, Trace trace) {
    enabledTrace.remove(trace);
    if (selector != null) {
      TraceData traceData = enabledTrace.get(trace);
      if (traceData != null)
        traceData.removeSelector(selector);
    }
  }

  synchronized public List<TraceData> selectTraces(TraceSelector selector, Set<Trace> oldSelection,
      Set<Trace> newSelection) {
    if (oldSelection != null) {
      Set<Trace> toRemove = new HashSet<Trace>(oldSelection);
      toRemove.removeAll(newSelection);
      removeTraces(selector, toRemove);
    }
    List<TraceData> traceDatas = new ArrayList<TraceData>();
    for (Trace trace : newSelection) {
      TraceData traceData = enableTrace(trace);
      traceDatas.add(traceData);
      if (oldSelection == null || !oldSelection.contains(trace))
        traceData.addSelector(selector);
    }
    purgeIFN();
    return traceDatas;
  }
}
