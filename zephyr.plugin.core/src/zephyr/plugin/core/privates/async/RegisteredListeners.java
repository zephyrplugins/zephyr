package zephyr.plugin.core.privates.async;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.EventListener;

public class RegisteredListeners {
  private final Map<String, List<EventListener>> eventIdRegistered = new LinkedHashMap<String, List<EventListener>>();
  private final Map<Event, List<EventListener>> eventRegistered = new LinkedHashMap<Event, List<EventListener>>();

  synchronized public void register(String id, EventListener listener) {
    List<EventListener> listeners = eventIdRegistered.get(id);
    if (listeners == null) {
      listeners = new ArrayList<EventListener>();
      eventIdRegistered.put(id, listeners);
    }
    listeners.add(listener);
  }

  synchronized public void unregister(String id, EventListener listener) {
    eventIdRegistered.get(id).remove(listener);
  }

  synchronized public void register(Event event, EventListener listener) {
    List<EventListener> listeners = eventRegistered.get(event);
    if (listeners == null) {
      listeners = new ArrayList<EventListener>();
      eventRegistered.put(event, listeners);
    }
    listeners.add(listener);
  }

  synchronized public void unregister(Event event, EventListener listener) {
    eventRegistered.get(event).remove(listener);
  }

  synchronized public Set<EventListener> getListeners(Event event) {
    Set<EventListener> listeners = new LinkedHashSet<EventListener>();
    List<EventListener> registeredListeners = eventRegistered.get(event);
    if (registeredListeners != null)
      listeners.addAll(registeredListeners);
    registeredListeners = eventIdRegistered.get(event.id());
    if (registeredListeners != null)
      listeners.addAll(registeredListeners);
    return listeners;
  }
}
