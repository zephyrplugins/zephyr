package zephyr.plugin.core.internal.async;

import java.util.concurrent.Semaphore;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.async.Event;
import zephyr.plugin.core.async.EventRecognizer;
import zephyr.plugin.core.async.EventWaiter;

public class ZephyrRecognizerReference implements EventWaiter {
  private final ZephyrBusEvent busEvent;
  Semaphore semaphore = new Semaphore(0);
  private Listener<Event> listener = null;
  final EventRecognizer recognizer;
  private boolean connected;

  public ZephyrRecognizerReference(ZephyrBusEvent busEvent, final EventRecognizer recognizer) {
    this.busEvent = busEvent;
    this.recognizer = recognizer;
    this.listener = new Listener<Event>() {
      @Override
      public void listen(Event eventProcessed) {
        if (recognizer.recognize(eventProcessed))
          semaphore.release();
      }
    };
  }

  @Override
  public void connect() {
    busEvent.processor().onEventProcessed.connect(listener);
    connected = true;
  }

  @Override
  public void waitForEvent() {
    if (!connected)
      throw new RuntimeException("Not connected");
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    busEvent.processor().onEventProcessed.disconnect(listener);
    connected = false;
  }
}
