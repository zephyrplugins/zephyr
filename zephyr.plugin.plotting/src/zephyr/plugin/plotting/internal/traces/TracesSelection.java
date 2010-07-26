package zephyr.plugin.plotting.internal.traces;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;

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
      removeTraces(traces);
    }
  };

  private boolean forceEnabled = ClockTracesManager.forceEnabled;
  private final Map<Trace, TraceData> enabledTrace = new ConcurrentHashMap<Trace, TraceData>();
  private final ClockTraces clockTraces;

  public TracesSelection(ClockTraces clockTraces) {
    this.clockTraces = clockTraces;
    clockTraces.onTraceAdded.connect(traceAddedListener);
    clockTraces.onTraceRemoved.connect(traceRemovedListener);
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
        enabledTrace.remove(trace);
    }
  }

  private Set<Entry<Trace, TraceData>> enabledEntries() {
    return new HashSet<Map.Entry<Trace, TraceData>>(enabledTrace.entrySet());
  }

  synchronized public void update(Clock clock) {
    for (TraceData traceData : enabledTrace.values())
      traceData.update(clock.time());
  }

  synchronized public void setForceEnabled(boolean forceEnabled) {
    this.forceEnabled = forceEnabled;
    if (!forceEnabled) {
      purgeIFN();
      return;
    }
    for (Trace trace : clockTraces.getTraces())
      enableTrace(trace);
  }

  synchronized protected void addTraces(List<Trace> traces) {
    if (!forceEnabled)
      return;
    for (Trace trace : traces)
      enableTrace(trace);
  }

  synchronized protected void removeTraces(List<Trace> traces) {
    for (Trace trace : traces)
      enabledTrace.remove(trace);
  }

  synchronized public List<TraceData> selectTraces(TraceSelector selector, Set<Trace> oldSelection,
      Set<Trace> newSelection) {
    if (oldSelection != null) {
      Set<Trace> toRemove = new HashSet<Trace>(oldSelection);
      toRemove.removeAll(newSelection);
      for (Trace trace : toRemove) {
        TraceData traceData = enabledTrace.get(trace);
        if (traceData != null)
          traceData.removeSelector(selector);
      }
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
