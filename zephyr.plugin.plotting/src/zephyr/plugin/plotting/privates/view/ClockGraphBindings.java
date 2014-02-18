package zephyr.plugin.plotting.privates.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.async.events.CastedEventListener;
import zephyr.plugin.core.internal.async.listeners.EventListener;
import zephyr.plugin.core.internal.events.ClockEvent;
import zephyr.plugin.plotting.privates.ZephyrPluginPlotting;
import zephyr.plugin.plotting.privates.traces.ClockTraces;
import zephyr.plugin.plotting.privates.traces.ClockTracesManager;
import zephyr.plugin.plotting.privates.traces.TraceData;

public class ClockGraphBindings {
  private final PlotView plotView;
  private final Set<Clock> clockTracesSelection = new LinkedHashSet<Clock>();
  private final Listener<List<TraceData>> selectionChangedListener = new Listener<List<TraceData>>() {
    @Override
    public void listen(List<TraceData> traceDatas) {
      selectionChanged(traceDatas);
    }
  };
  private final EventListener clockRemovedListener = new CastedEventListener<ClockEvent>() {
    @Override
    protected void listenEvent(ClockEvent event) {
      unbind(event.clock());
    }
  };

  public ClockGraphBindings(PlotView plotView) {
    this.plotView = plotView;
    plotView.plotSelection.onSelectedTracesChanged.connect(selectionChangedListener);
    ZephyrSync.busEvent().register(ClockEvent.RemovedID, clockRemovedListener);
  }

  private void bind(Clock clock) {
    ZephyrSync.bind(clock, plotView);
    boolean added = clockTracesSelection.add(clock);
    assert added;
  }

  protected void unbind(Clock clock) {
    if (clockTracesSelection.remove(clock)) {
      plotView.plotSelection.checkRemovedTrace(clock);
      ZephyrSync.unbind(clock, plotView);
      ZephyrSync.submitView(plotView);
    }
  }

  protected void unBindAll() {
    for (Clock clock : new ArrayList<Clock>(clockTracesSelection))
      unbind(clock);
    assert clockTracesSelection.isEmpty();
  }


  protected void selectionChanged(List<TraceData> traceDatas) {
    ClockTracesManager tracesManager = ZephyrPluginPlotting.tracesManager();
    Set<ClockTraces> clocks = new HashSet<ClockTraces>();
    for (TraceData traceData : traceDatas)
      clocks.add(tracesManager.clockTraces(traceData.trace.clock()));
    bind(clocks);
  }

  private void bind(Set<ClockTraces> clocks) {
    unBindAll();
    for (ClockTraces clockTraces : clocks)
      bind(clockTraces.clock);
  }

  public void dispose() {
    plotView.plotSelection.onSelectedTracesChanged.disconnect(selectionChangedListener);
    ZephyrSync.busEvent().unregister(ClockEvent.RemovedID, clockRemovedListener);
  }

  public boolean isEmpty() {
    return clockTracesSelection.isEmpty();
  }

  public Clock[] clocks() {
    Clock[] clocks = new Clock[clockTracesSelection.size()];
    clockTracesSelection.toArray(clocks);
    return clocks;
  }
}
