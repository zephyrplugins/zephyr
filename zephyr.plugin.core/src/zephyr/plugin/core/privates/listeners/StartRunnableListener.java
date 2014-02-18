package zephyr.plugin.core.privates.listeners;

import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.EventListener;
import zephyr.plugin.core.internal.events.StartRunnableEvent;
import zephyr.plugin.core.privates.ZephyrPluginCore;

public class StartRunnableListener implements EventListener {
  @Override
  public void listen(Event eventInfo) {
    ZephyrPluginCore.getDefault().startZephyrMain(((StartRunnableEvent) eventInfo).factory);
  }
}
