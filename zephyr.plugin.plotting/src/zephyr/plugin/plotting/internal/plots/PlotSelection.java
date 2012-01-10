package zephyr.plugin.plotting.internal.plots;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ui.IMemento;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;
import zephyr.plugin.core.events.CodeStructureEvent;
import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;
import zephyr.plugin.plotting.internal.traces.ClockTraces;
import zephyr.plugin.plotting.internal.traces.PersistentTrace;
import zephyr.plugin.plotting.internal.traces.Trace;
import zephyr.plugin.plotting.internal.traces.TraceData;
import zephyr.plugin.plotting.internal.traces.Traces;

public class PlotSelection {
  final private static String SelectionTypeKey = "selection";

  public Signal<List<TraceData>> onSelectedTracesChanged = new Signal<List<TraceData>>();
  public Signal<Integer> onHistoryChanged = new Signal<Integer>();
  private final Set<PersistentTrace> persistentSelection = new LinkedHashSet<PersistentTrace>();
  private final List<TraceData> selected = new ArrayList<TraceData>();
  private final EventListener addedTraceListener = new EventListener() {
    @Override
    public void listen(Event event) {
      checkNewTrace(((CodeStructureEvent) event).clock());
    }
  };
  private final EventListener removedTraceListener = new EventListener() {
    @Override
    public void listen(Event event) {
      checkRemovedTrace(((CodeStructureEvent) event).clock());
    }
  };

  public PlotSelection() {
    ZephyrCore.busEvent().register(CodeStructureEvent.ParsedID, addedTraceListener);
    ZephyrCore.busEvent().register(CodeStructureEvent.RemovedID, removedTraceListener);
  }

  synchronized void checkNewTrace(Clock clock) {
    Set<Trace> selection = getCurrentTracesSelection();
    boolean changed = false;
    for (PersistentTrace trace : persistentSelection) {
      CodeNode codeNode = ZephyrCore.syncCode().findNode(trace.path);
      if (codeNode == null)
        continue;
      changed = selection.add(new Trace(trace.label, codeNode)) || changed;
    }
    if (changed)
      setCurrentSelection(selection);
  }

  synchronized protected void checkRemovedTrace(Clock clock) {
  }

  synchronized public void setCurrentSelection(Set<Trace> newSelection) {
    derefSelection();
    Map<ClockTraces, Set<Trace>> orderedNewTraces = Traces.orderTraces(newSelection);
    for (Map.Entry<ClockTraces, Set<Trace>> entry : orderedNewTraces.entrySet()) {
      ClockTraces clockTraces = entry.getKey();
      for (Trace trace : entry.getValue()) {
        persistentSelection.remove(new PersistentTrace(trace.label, trace.path()));
        TraceData traceData = clockTraces.traceData(trace);
        if (traceData == null)
          continue;
        traceData.incRef();
        selected.add(traceData);
      }
    }
    ZephyrPluginPlotting.tracesManager().gc();
    fireSelectedTracesChanged();
  }

  synchronized private void derefSelection() {
    for (TraceData traceData : selected)
      traceData.decRef();
    selected.clear();
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

  public void init(IMemento memento) {
    for (IMemento child : memento.getChildren(SelectionTypeKey)) {
      PersistentTrace loaded = PersistentTrace.load(child);
      if (loaded == null)
        continue;
      persistentSelection.add(loaded);
    }
    for (Clock clock : ZephyrCore.registeredClocks())
      checkNewTrace(clock);
  }

  public void saveState(IMemento memento) {
    for (PersistentTrace trace : persistentSelection)
      PersistentTrace.save(memento.createChild(SelectionTypeKey), trace.label, trace.path);
    for (TraceData traceData : selected)
      PersistentTrace.save(memento.createChild(SelectionTypeKey), traceData.trace.label, traceData.trace.path());
  }

  public void dispose() {
    derefSelection();
    ZephyrPluginPlotting.tracesManager().gc();
    fireSelectedTracesChanged();
  }
}
