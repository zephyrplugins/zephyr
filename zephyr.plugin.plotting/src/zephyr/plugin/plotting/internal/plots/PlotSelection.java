package zephyr.plugin.plotting.internal.plots;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.plotting.internal.traces.ClockTraces;
import zephyr.plugin.plotting.internal.traces.ClockTracesManager;
import zephyr.plugin.plotting.internal.traces.Trace;
import zephyr.plugin.plotting.internal.traces.TraceData;
import zephyr.plugin.plotting.internal.traces.Traces;
import zephyr.plugin.plotting.internal.traces.TracesSelection;
import zephyr.plugin.plotting.internal.traces.TracesSelection.TraceSelector;

public class PlotSelection implements TraceSelector {
  public Signal<List<TraceData>> onSelectedTracesChanged = new Signal<List<TraceData>>();
  public Signal<Integer> onHistoryChanged = new Signal<Integer>();
  private final List<TraceData> selected = new ArrayList<TraceData>();
  private final Set<String> persistentSelection = new LinkedHashSet<String>();
  private final Set<String> currentSelection = new LinkedHashSet<String>();
  private final Listener<List<Trace>> addedTraceListener = new Listener<List<Trace>>() {
    @Override
    public void listen(List<Trace> traces) {
      checkNewTrace(traces);
    }
  };
  private final Listener<List<Trace>> removedTraceListener = new Listener<List<Trace>>() {
    @Override
    public void listen(List<Trace> traces) {
      checkRemovedTrace(traces);
    }
  };
  private final ClockTracesManager tracesManager;

  public PlotSelection(ClockTracesManager tracesManager) {
    this.tracesManager = tracesManager;
  }

  synchronized public Set<String> getLabelsToSave() {
    Set<String> labelsToSave = new LinkedHashSet<String>();
    labelsToSave.addAll(persistentSelection);
    labelsToSave.addAll(currentSelection);
    return labelsToSave;
  }

  synchronized public void init(Set<String> initialSelection) {
    if (initialSelection != null)
      persistentSelection.addAll(initialSelection);
    checkNewTrace(Traces.getAllTraces());
    tracesManager.onTraceAdded.connect(addedTraceListener);
    tracesManager.onTraceRemoved.connect(removedTraceListener);
  }

  synchronized void checkNewTrace(List<Trace> traces) {
    List<Trace> tracesAdded = new ArrayList<Trace>();
    for (Trace trace : traces) {
      if (!persistentSelection.contains(trace.label))
        continue;
      persistentSelection.remove(trace.label);
      currentSelection.add(trace.label);
      tracesAdded.add(trace);
    }
    if (tracesAdded.isEmpty())
      return;
    Set<Trace> selectedTraces = getCurrentTracesSelection();
    selectedTraces.addAll(tracesAdded);
    setCurrentSelection(selectedTraces);
  }

  synchronized protected void checkRemovedTrace(List<Trace> traces) {
    Set<Trace> currentSelection = getCurrentTracesSelection();
    boolean oneTraceRemoved = false;
    for (Trace trace : traces) {
      boolean removed = currentSelection.remove(trace);
      if (removed) {
        oneTraceRemoved = true;
        persistentSelection.add(trace.label);
      }
    }
    if (oneTraceRemoved)
      setCurrentSelection(currentSelection);
  }

  synchronized public void setCurrentSelection(Set<Trace> newSelection) {
    Map<ClockTraces, Set<Trace>> orderedNewTraces = Traces.orderTraces(newSelection);
    Map<ClockTraces, Set<Trace>> orderedOldTraces = Traces.orderTraces(selected);
    for (ClockTraces clockTrace : orderedOldTraces.keySet())
      if (!orderedNewTraces.containsKey(clockTrace))
        orderedNewTraces.put(clockTrace, new HashSet<Trace>());
    selected.clear();
    currentSelection.clear();
    for (Map.Entry<ClockTraces, Set<Trace>> entry : orderedNewTraces.entrySet()) {
      final ClockTraces clockTrace = entry.getKey();
      TracesSelection selection = clockTrace.selection();
      final Set<Trace> newSel = entry.getValue();
      final Set<Trace> oldSel = orderedOldTraces.get(clockTrace);
      List<TraceData> selectedTraceData = selection.selectTraces(this, oldSel, newSel);
      for (TraceData traceData : selectedTraceData)
        currentSelection.add(traceData.trace.label);
      selected.addAll(selectedTraceData);
    }
    fireSelectedTracesChanged();
  }

  protected void fireSelectedTracesChanged() {
    onSelectedTracesChanged.fire(selected);
  }

  synchronized public List<TraceData> getSelection() {
    return new ArrayList<TraceData>(selected);
  }

  synchronized public Set<Trace> getCurrentTracesSelection() {
    Set<Trace> traceSelected = new LinkedHashSet<Trace>();
    for (TraceData traceData : selected)
      traceSelected.add(traceData.trace);
    return traceSelected;
  }

  synchronized public boolean isEmpty() {
    return selected.isEmpty();
  }

  public void dispose() {
    setCurrentSelection(new HashSet<Trace>());
  }
}
