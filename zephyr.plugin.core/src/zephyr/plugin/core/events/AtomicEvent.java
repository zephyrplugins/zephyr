package zephyr.plugin.core.events;

import zephyr.plugin.core.async.events.Event;

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
