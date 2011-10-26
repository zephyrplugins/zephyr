package zephyr.plugin.junittesting.busevent;

import zephyr.plugin.core.async.BusEvent;
import zephyr.plugin.core.async.Event;
import zephyr.plugin.core.async.EventListener;
import zephyr.plugin.core.async.ListenerRecorder;

public class JUnitRecorder implements ListenerRecorder {
  public static final EventListener Listener = new EventListener() {
    @Override
    public void listen(Event event) {
      ((TestingEvent) event).process();
    }
  };

  @Override
  public void recordListener(BusEvent busEvent) {
    busEvent.register(TestingEvent.EventID, Listener);
    busEvent.register(TestingEvent.EventID, Listener);
  }
}
