package zephyr.plugin.plotting.privates.listeners;

import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.EventListener;
import zephyr.plugin.core.internal.events.CodeStructureEvent;
import zephyr.plugin.plotting.privates.ZephyrPluginPlotting;
import zephyr.plugin.plotting.privates.traces.ClockTracesManager;

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
