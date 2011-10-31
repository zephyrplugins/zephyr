package zephyr.plugin.junittesting.busevent;

import zephyr.plugin.core.async.Event;
import zephyr.plugin.core.async.EventListener;

public class TestingEventListener implements EventListener {
  static TestingEventListener listener;

  public TestingEventListener() {
    if (listener == null)
      listener = this;
  }

  @Override
  public void listen(Event event) {
    ((TestingEvent) event).process();
  }
}
