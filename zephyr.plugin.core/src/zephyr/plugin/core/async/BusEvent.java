package zephyr.plugin.core.async;


public interface BusEvent {
  void register(String id, EventListener listener);

  void unregister(String id, EventListener listener);

  void register(Event event, EventListener listener);

  void unregister(Event event, EventListener listener);

  void dispatch(Event event);

  void syncDispatch(Event event);

  EventWaiter createWaiter(EventRecognizer recognizer);
}