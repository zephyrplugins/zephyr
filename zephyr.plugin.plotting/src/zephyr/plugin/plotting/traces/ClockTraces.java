package zephyr.plugin.plotting.traces;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rlpark.plugin.utils.Utils;
import rlpark.plugin.utils.events.Listener;
import rlpark.plugin.utils.events.Signal;
import rlpark.plugin.utils.logger.LabelBuilder;
import rlpark.plugin.utils.logger.abstracts.Logged;
import rlpark.plugin.utils.logger.abstracts.LoggedContainer;
import rlpark.plugin.utils.logger.abstracts.Logger;
import rlpark.plugin.utils.logger.helpers.Parser;
import rlpark.plugin.utils.time.Clock;
import zephyr.plugin.plotting.ZephyrPluginPlotting;

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
  synchronized public void add(String label, Logged logged) {
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
    if (toAdd instanceof Logged)
      add(Utils.label(toAdd), (Logged) toAdd);
    if (toAdd instanceof LoggedContainer)
      ((LoggedContainer) toAdd).setLogger(this);
    Parser.findAnnotations(this, toAdd);
    endAddingTrace();
  }

  @Override
  public LabelBuilder labelBuilder() {
    return labelBuilder;
  }

  @Override
  public Logger newClock(String clockLabel, Clock clock) {
    assert checkThread();
    Logger loggedManager = ZephyrPluginPlotting.clockTracesManager().addClock(labelBuilder.prefixed(clockLabel),
                                                                                     clock);
    return loggedManager;
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
