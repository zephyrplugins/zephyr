package zephyr.plugin.core.internal.async;

import java.util.Set;

import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.async.Event;
import zephyr.plugin.core.async.EventListener;

public class EventProcessor implements Runnable {
  private final RegisteredListeners registeredListeners;
  private final PendingEvents pendingEvents;
  public Signal<Event> onEventProcessed = new Signal<Event>();

  public EventProcessor(PendingEvents pendingEvents, RegisteredListeners registeredListeners) {
    this.pendingEvents = pendingEvents;
    this.registeredListeners = registeredListeners;
  }

  @Override
  public void run() {
    while (true)
      processEvent(pendingEvents.waitEvent());
  }

  public void processEvent(Event event) {
    Set<EventListener> listeners = registeredListeners.getListeners(event);
    for (EventListener listener : listeners)
      listener.listen(event);
    onEventProcessed.fire(event);
  }
}
