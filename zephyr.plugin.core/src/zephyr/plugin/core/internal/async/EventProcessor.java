package zephyr.plugin.core.internal.async;

import java.util.Set;

import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;

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
      process(event, listener);
    onEventProcessed.fire(event);
  }

  private void process(Event event, EventListener listener) {
    try {
      listener.listen(event);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }
}
