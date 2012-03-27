package zephyr.plugin.core.internal.events;

import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.async.events.Event;

public class ClockEvent implements Event {
  public static final String AddedID = "zephyr.event.clockadded";
  public static final String RemovedID = "zephyr.event.clockremoved";
  private final Clock clock;
  private final String id;

  public ClockEvent(String id, Clock clock) {
    this.id = id;
    this.clock = clock;
  }

  @Override
  public String id() {
    return id;
  }

  public Clock clock() {
    return clock;
  }
}
