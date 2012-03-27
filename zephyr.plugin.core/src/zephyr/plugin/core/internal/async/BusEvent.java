package zephyr.plugin.core.internal.async;

import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.EventListener;
import zephyr.plugin.core.internal.async.recognizers.EventRecognizer;
import zephyr.plugin.core.internal.async.recognizers.OnEventBlocker;


public interface BusEvent {
  void register(String id, EventListener listener);

  void unregister(String id, EventListener listener);

  void register(Event event, EventListener listener);

  void unregister(Event event, EventListener listener);

  void dispatch(Event event);

  void syncDispatch(Event event);

  OnEventBlocker createWaiter(EventRecognizer recognizer);
}