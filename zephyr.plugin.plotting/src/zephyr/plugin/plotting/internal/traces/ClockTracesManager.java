package zephyr.plugin.plotting.internal.traces;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.codetree.PrimitiveNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitorAdapter;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorSynchronizer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.abstracts.MonitoredDataTraverser;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;
import zephyr.plugin.core.events.ClockEvent;

public class ClockTracesManager implements MonitorSynchronizer {
  private boolean forceEnabled = false;
  private final Map<Clock, ClockTraces> clocks = new LinkedHashMap<Clock, ClockTraces>();
  private final Set<TraceData> forcedMonitored = new LinkedHashSet<TraceData>();

  public ClockTracesManager() {
    ZephyrCore.busEvent().register(ClockEvent.RemovedID, new EventListener() {

      @Override
      public void listen(Event event) {
        removeClock(((ClockEvent) event).clock());
      }
    });
  }

  synchronized public void removeClock(Clock clock) {
    ClockTraces clockTraces = clocks.remove(clock);
    if (clockTraces != null)
      clockTraces.dispose();
  }

  synchronized public List<ClockTraces> getClockTraces() {
    return new ArrayList<ClockTraces>(clocks.values());
  }

  public boolean forceEnabled() {
    return forceEnabled;
  }

  synchronized public ClockTraces clockTraces(Clock clock) {
    Zephyr.advertise(clock, null);
    ClockTraces clockTraces = clocks.get(clock);
    if (clockTraces != null)
      return clockTraces;
    clockTraces = new ClockTraces(clock);
    assert !clocks.containsKey(clock);
    clocks.put(clock, clockTraces);
    return clockTraces;
  }

  @Override
  public DataMonitor getSyncMonitor(Clock clock) {
    final ClockNode clockNode = ZephyrCore.syncCode().clockNode(clock);
    return new DataMonitor() {
      @Override
      public void add(String label, Monitored monitored) {
        clockNode.addChild(new PrimitiveNode(label, clockNode, monitored, 1));
      }
    };
  }

  synchronized void reference(ClockTraces clockTraces, Trace trace, Monitored monitored) {
    TraceData traceData = clockTraces.traceData(trace, monitored);
    traceData.incRef();
    forcedMonitored.add(traceData);
  }

  synchronized public void setForceEnabled(boolean booleanState) {
    forceEnabled = booleanState;
    if (forceEnabled)
      findNewTraces();
    else
      decrefTraces();
  }

  synchronized private void decrefTraces() {
    for (TraceData traceData : forcedMonitored)
      traceData.decRef();
    gc();
  }

  private void findNewTraces() {
    for (Clock clock : clocks.keySet())
      registerTraces(clock, ZephyrCore.syncCode().clockNode(clock));
  }

  public void registerTraces(Clock clock, CodeNode codeNode) {
    final ClockTraces clockTraces = clockTraces(clock);
    MonitoredDataTraverser traverser = new MonitoredDataTraverser(new DataMonitorAdapter() {
      @Override
      public void add(MonitorContainerNode codeNode) {
        String[] labels = codeNode.createLabels();
        Monitored[] monitored = codeNode.createMonitored();
        for (int i = 0; i < monitored.length; i++)
          reference(clockTraces, new Trace(labels[i], (CodeNode) codeNode), monitored[i]);
      }
    }, 1);
    CodeTrees.traverse(traverser, codeNode);
  }

  synchronized public void gc() {
    for (ClockTraces clockTraces : clocks.values())
      clockTraces.gc();
  }
}
