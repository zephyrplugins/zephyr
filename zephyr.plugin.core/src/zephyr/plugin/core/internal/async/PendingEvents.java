package zephyr.plugin.core.internal.async;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import zephyr.plugin.core.async.events.Event;

public class PendingEvents {
  private final HashSet<Event> pendingEvents = new HashSet<Event>();
  private final Queue<Event> orderedEvents = new LinkedList<Event>();
  private final Semaphore semaphore = new Semaphore(0);

  synchronized public void dispatch(Event event) {
    boolean added = pendingEvents.add(event);
    if (!added)
      return;
    orderedEvents.add(event);
    semaphore.release();
  }

  public Event waitEvent() {
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    }
    return pullEvent();
  }

  synchronized private Event pullEvent() {
    Event event = orderedEvents.poll();
    boolean removed = pendingEvents.remove(event);
    assert removed;
    assert event != null;
    assert semaphore.availablePermits() == pendingEvents.size();
    assert orderedEvents.size() == pendingEvents.size();
    return event;
  }
}
