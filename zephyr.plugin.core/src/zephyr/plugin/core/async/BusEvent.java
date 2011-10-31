package zephyr.plugin.core.async;

import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;
import zephyr.plugin.core.async.recognizers.EventRecognizer;
import zephyr.plugin.core.async.recognizers.OnEventBlocker;


public interface BusEvent {
  void register(String id, EventListener listener);

  void unregister(String id, EventListener listener);

  void register(Event event, EventListener listener);

  void unregister(Event event, EventListener listener);

  void dispatch(Event event);

  void syncDispatch(Event event);

  OnEventBlocker createWaiter(EventRecognizer recognizer);
}