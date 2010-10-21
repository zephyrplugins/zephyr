package zephyr.plugin.plotting.internal.graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import zephyr.ZephyrSync;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.plotting.internal.traces.ClockTraces;
import zephyr.plugin.plotting.internal.traces.TraceData;

public class ClockGraphBindings {
  private final PlotView plotView;
  private final Set<Clock> clockTracesSelection = new LinkedHashSet<Clock>();
  private final Listener<List<TraceData>> selectionChangedListener = new Listener<List<TraceData>>() {
    @Override
    public void listen(List<TraceData> traceDatas) {
      selectionChanged(traceDatas);
    }
  };

  public ClockGraphBindings(PlotView plotView) {
    this.plotView = plotView;
    plotView.plotSelection.onSelectedTracesChanged.connect(selectionChangedListener);
  }

  private void bind(Clock clock) {
    ZephyrSync.bind(clock, plotView);
    boolean added = clockTracesSelection.add(clock);
    assert added;
  }

  protected void unbind(Clock clock) {
    if (clockTracesSelection.remove(clock))
      ZephyrSync.unbind(clock, plotView);
  }

  protected void unBindAll() {
    for (Clock clock : new ArrayList<Clock>(clockTracesSelection))
      unbind(clock);
    assert clockTracesSelection.isEmpty();
  }


  protected void selectionChanged(List<TraceData> traceDatas) {
    Set<ClockTraces> clocks = new HashSet<ClockTraces>();
    for (TraceData traceData : traceDatas)
      clocks.add(traceData.trace.clockTraces);
    bind(clocks);
  }

  private void bind(Set<ClockTraces> clocks) {
    unBindAll();
    for (ClockTraces clockTraces : clocks)
      bind(clockTraces.clock);
  }

  public void dispose() {
    plotView.plotSelection.onSelectedTracesChanged.disconnect(selectionChangedListener);
  }

  public boolean isEmpty() {
    return clockTracesSelection.isEmpty();
  }
}
