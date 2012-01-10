package zephyr.plugin.plotting.internal.listeners;

import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;
import zephyr.plugin.core.events.CodeStructureEvent;
import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;
import zephyr.plugin.plotting.internal.traces.ClockTracesManager;

public class RegisterPlottingMonitor implements EventListener {
  @Override
  public void listen(Event event) {
    ClockTracesManager manager = ZephyrPluginPlotting.tracesManager();
    if (!manager.forceEnabled())
      return;
    CodeStructureEvent eventInfo = (CodeStructureEvent) event;
    manager.registerTraces(eventInfo.clock(), eventInfo.node());
  }
}
