package zephyr.plugin.core.internal.events;

import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.internal.async.events.Event;

public class StartRunnableEvent implements Event {
  public static final String ID = "zephyr.event.startrunnable";
  public final RunnableFactory factory;

  public StartRunnableEvent(RunnableFactory factory) {
    this.factory = factory;
  }

  @Override
  public String id() {
    return ID;
  }
}
