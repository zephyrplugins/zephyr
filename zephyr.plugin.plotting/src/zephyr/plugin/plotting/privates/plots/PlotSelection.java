package zephyr.plugin.plotting.privates.plots;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.ui.IMemento;
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.EventListener;
import zephyr.plugin.core.internal.events.CodeStructureEvent;
import zephyr.plugin.plotting.privates.ZephyrPluginPlotting;
import zephyr.plugin.plotting.privates.traces.ClockTraces;
import zephyr.plugin.plotting.privates.traces.PersistentTrace;
import zephyr.plugin.plotting.privates.traces.Trace;
import zephyr.plugin.plotting.privates.traces.TraceData;
import zephyr.plugin.plotting.privates.traces.Traces;

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
      derefSelection(((CodeStructureEvent) event).clock(), true);
    }
  };

  public PlotSelection() {
    ZephyrSync.busEvent().register(CodeStructureEvent.ParsedID, addedTraceListener);
    ZephyrSync.busEvent().register(CodeStructureEvent.RemovedID, removedTraceListener);
  }

  synchronized void checkNewTrace(Clock clock) {
    Set<Trace> selection = getCurrentTracesSelection();
    boolean changed = false;
    for (PersistentTrace trace : persistentSelection) {
      CodeNode codeNode = ZephyrSync.syncCode().findNode(trace.path);
      if (codeNode == null)
        continue;
      changed = selection.add(new Trace(trace.label, codeNode)) || changed;
    }
    if (changed)
      setCurrentSelection(selection);
  }

  synchronized public void setCurrentSelection(Set<Trace> newSelection) {
    derefSelection(false);
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

  public void derefSelection(boolean saveSelection) {
    derefSelection(null, saveSelection);
  }

  synchronized public void derefSelection(Clock clock, boolean saveSelection) {
    for (TraceData traceData : selected) {
      if (clock != null && traceData.trace.clock() != clock)
        continue;
      traceData.decRef();
      if (saveSelection)
        persistentSelection.add(new PersistentTrace(traceData.trace.label, traceData.trace.path()));
    }
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
    derefSelection(true);
    ZephyrPluginPlotting.tracesManager().gc();
    fireSelectedTracesChanged();
  }
}
