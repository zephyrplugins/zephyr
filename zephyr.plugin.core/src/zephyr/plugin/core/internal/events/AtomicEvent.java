package zephyr.plugin.core.internal.events;

import zephyr.plugin.core.internal.async.events.Event;

public class AtomicEvent implements Event {
  private final String id;

  public AtomicEvent(String id) {
    this.id = id;
  }

  @Override
  public String id() {
    return id;
  }
}
