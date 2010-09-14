package zephyr.plugin.plotting.internal.traces;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import zephyr.plugin.core.Utils;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.logging.LabelBuilder;
import zephyr.plugin.core.api.logging.abstracts.LoggedContainer;
import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.logging.abstracts.Monitored;
import zephyr.plugin.core.api.logging.helpers.Parser;
import zephyr.plugin.core.api.logging.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class ClockTraces implements Logger {
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
  synchronized public void add(String label, Monitored logged) {
    assert checkThread();
    Trace trace = new Trace(this, labelBuilder.buildLabel(label), logged);
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
    startAddingTrace();
    if (toAdd instanceof Monitored)
      add(Labels.label(toAdd), (Monitored) toAdd);
    if (toAdd instanceof LoggedContainer)
      ((LoggedContainer) toAdd).setLogger(this);
    Parser.findAnnotations(this, toAdd, new ArrayList<MonitorWrapper>());
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
