package zephyr.plugin.plotting.internal.listeners;

import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.monitoring.abstracts.MonitoredDataTraverser;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;
import zephyr.plugin.core.events.CodeStructureEvent;
import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;
import zephyr.plugin.plotting.internal.traces.ClockTraces;
import zephyr.plugin.plotting.internal.traces.ClockTracesManager;

public class RegisterPlottingMonitor implements EventListener {
  @Override
  public void listen(Event event) {
    CodeStructureEvent eventInfo = (CodeStructureEvent) event;
    ClockTracesManager manager = ZephyrPluginPlotting.tracesManager();
    Clock clock = eventInfo.clock();
    ClockTraces dataMonitor = manager.getSyncMonitor(clock);
    dataMonitor.startAddingTrace();
    MonitoredDataTraverser traverser = new MonitoredDataTraverser(dataMonitor);
    CodeTrees.traverse(traverser, eventInfo.node());
    dataMonitor.endAddingTrace();
  }
}
