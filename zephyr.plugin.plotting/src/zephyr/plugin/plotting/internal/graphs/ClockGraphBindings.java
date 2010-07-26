package zephyr.plugin.plotting.internal.graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import zephyr.plugin.core.ZephyrPluginCommon;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.ClockKillable;
import zephyr.plugin.core.views.ViewBinder;
import zephyr.plugin.plotting.internal.traces.ClockTraces;
import zephyr.plugin.plotting.internal.traces.TraceData;

public class ClockGraphBindings {

  private final PlotView plotView;
  private final Set<Clock> clockTracesSelection = new LinkedHashSet<Clock>();
  private final ViewBinder binder = ZephyrPluginCommon.viewBinder();
  private final Listener<Clock> clockKilled = new Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      unbind(clock);
    }
  };
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
    binder.bind(clock, plotView);
    if (clock instanceof ClockKillable)
      ((ClockKillable) clock).onKill.connect(clockKilled);
    boolean added = clockTracesSelection.add(clock);
    assert added;
  }

  protected void unbind(Clock clock) {
    if (clockTracesSelection.remove(clock)) {
      binder.unbind(clock, plotView);
      if (clock instanceof ClockKillable)
        ((ClockKillable) clock).onKill.disconnect(clockKilled);
    }
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
