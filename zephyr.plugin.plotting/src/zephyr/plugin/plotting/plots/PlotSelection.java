package zephyr.plugin.plotting.plots;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rlpark.plugin.utils.events.Listener;
import rlpark.plugin.utils.events.Signal;
import zephyr.plugin.plotting.traces.ClockTraces;
import zephyr.plugin.plotting.traces.ClockTracesManager;
import zephyr.plugin.plotting.traces.Trace;
import zephyr.plugin.plotting.traces.TraceData;
import zephyr.plugin.plotting.traces.Traces;
import zephyr.plugin.plotting.traces.TracesSelection;
import zephyr.plugin.plotting.traces.TracesSelection.TraceSelector;

public class PlotSelection implements TraceSelector {
  public Signal<List<TraceData>> onSelectedTracesChanged = new Signal<List<TraceData>>();
  public Signal<Integer> onHistoryChanged = new Signal<Integer>();
  private final List<TraceData> selected = new ArrayList<TraceData>();
  private final Set<String> persistentSelection = new LinkedHashSet<String>();
  private final Set<String> currentSelection = new LinkedHashSet<String>();
  private final Listener<Trace> addedTraceListener = new Listener<Trace>() {
    @Override
    public void listen(Trace trace) {
      checkNewTrace(trace);
    }
  };
  private final Listener<Trace> removedTraceListener = new Listener<Trace>() {
    @Override
    public void listen(Trace trace) {
      checkRemovedTrace(trace);
    }
  };
  private final ClockTracesManager tracesManager;

  public PlotSelection(ClockTracesManager tracesManager) {
    this.tracesManager = tracesManager;
  }

  public boolean isEmpty() {
    return (selected == null || selected.isEmpty());
  }

  public Set<String> getLabelsToSave() {
    Set<String> labelsToSave = new LinkedHashSet<String>();
    labelsToSave.addAll(persistentSelection);
    labelsToSave.addAll(currentSelection);
    return labelsToSave;
  }

  public int size() {
    return selected.size();
  }

  public TraceData get(int i) {
    return selected.get(i);
  }

  public void init(Set<String> initialSelection) {
    if (initialSelection != null)
      this.persistentSelection.addAll(initialSelection);
    for (Trace trace : Traces.getAllTraces(tracesManager))
      checkNewTrace(trace);
    tracesManager.onTraceAdded.connect(addedTraceListener);
    tracesManager.onTraceRemoved.connect(removedTraceListener);
  }

  void checkNewTrace(Trace trace) {
    if (persistentSelection.contains(trace.label)) {
      persistentSelection.remove(trace.label);
      currentSelection.add(trace.label);
    }
    if (!currentSelection.contains(trace.label))
      return;
    Set<Trace> selectedTraces = getCurrentTracesSelection();
    selectedTraces.add(trace);
    setCurrentSelection(selectedTraces);
  }

  protected void checkRemovedTrace(Trace trace) {
    Set<Trace> currentSelection = getCurrentTracesSelection();
    boolean removed = currentSelection.remove(trace);
    if (!removed)
      return;
    persistentSelection.add(trace.label);
    setCurrentSelection(currentSelection);
  }

  public void setCurrentSelection(Set<Trace> newSelection) {
    Map<ClockTraces, Set<Trace>> orderedNewTraces = Traces.orderTraces(newSelection);
    Map<ClockTraces, Set<Trace>> orderedOldTraces = Traces.orderTraces(selected);
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

  public Set<Trace> getCurrentTracesSelection() {
    Set<Trace> traceSelected = new LinkedHashSet<Trace>();
    for (TraceData traceData : selected)
      traceSelected.add(traceData.trace);
    return traceSelected;
  }
}
