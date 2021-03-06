package zephyr.plugin.junittesting.busevent;

import org.junit.Assert;
import zephyr.plugin.core.internal.async.events.Event;

public class TestingEvent implements Event {
  static public String EventID = "TestBusEvent";
  private boolean processed = false;
  private final String id;

  public TestingEvent() {
    this(EventID);
  }

  public TestingEvent(String id) {
    this.id = id;
  }

  public void process() {
    Assert.assertFalse(processed);
    processed = true;
  }

  public boolean processed() {
    return processed;
  }

  @Override
  public String id() {
    return id;
  }
}
