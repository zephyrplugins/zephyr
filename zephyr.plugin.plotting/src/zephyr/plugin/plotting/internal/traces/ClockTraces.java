package zephyr.plugin.plotting.internal.traces;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import zephyr.plugin.core.Utils;
import zephyr.plugin.core.api.monitoring.LabelBuilder;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.helpers.Parser;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class ClockTraces implements DataMonitor {
  public final Signal<List<Trace>> onTraceAdded = new Signal<List<Trace>>();
  public final Signal<List<Trace>> onTraceRemoved = new Signal<List<Trace>>();
  public final Clock clock;

  protected final TracesSelection selection;
  private final LabelBuilder labelBuilder = new LabelBuilder();
  private final List<Trace> traces = new LinkedList<Trace>();
  private final Listener<Clock> onTickClockListener = new Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      selection.update(clock);
    }
  };
  private final Thread modelThread = Thread.currentThread();
  private final String clockLabel;
  private int nbProcessAddingTrace = 0;
  private final List<Trace> pendingTraces = new ArrayList<Trace>();

  protected ClockTraces(String clockLabel, Clock clock) {
    this.clock = clock;
    this.clockLabel = clockLabel;
    labelBuilder.push(clockLabel);
    selection = new TracesSelection(this);
    clock.onTick.connect(onTickClockListener);
  }

  private void startAddingTrace() {
    nbProcessAddingTrace++;
  }

  private void endAddingTrace() {
    assert nbProcessAddingTrace > 0;
    nbProcessAddingTrace--;
    if (nbProcessAddingTrace > 0)
      return;
    if (pendingTraces.isEmpty())
      return;
    onTraceAdded.fire(pendingTraces);
    pendingTraces.clear();
  }

  @Override
  synchronized public void add(String label, Monitored logged, int level) {
    assert checkThread();
    Trace trace = new Trace(this, labelBuilder.buildLabel(label), logged, level);
    traces.add(trace);
    if (nbProcessAddingTrace == 0)
      onTraceAdded.fire(Utils.asList(trace));
    else
      pendingTraces.add(trace);
  }

  protected boolean checkThread() {
    return modelThread == Thread.currentThread();
  }

  @Override
  public void add(Object toAdd) {
    add(toAdd, Parser.MonitorEverythingLevel);
  }

  @Override
  public void add(Object toAdd, int level) {
    startAddingTrace();
    Parser.parse(this, toAdd, level);
    endAddingTrace();
  }

  @Override
  public LabelBuilder labelBuilder() {
    return labelBuilder;
  }

  synchronized public List<Trace> getTraces() {
    return new ArrayList<Trace>(traces);
  }

  synchronized public void dispose() {
    onTraceRemoved.fire(traces);
    traces.clear();
  }

  public String clockLabel() {
    return clockLabel;
  }

  public TracesSelection selection() {
    return selection;
  }
}
