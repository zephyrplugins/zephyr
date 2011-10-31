package zephyr.plugin.core.async.events;

import zephyr.plugin.core.async.listeners.EventListener;

public abstract class CastedEventListener<T> implements EventListener {
  @SuppressWarnings("unchecked")
  @Override
  public void listen(Event eventInfo) {
    listenEvent((T) eventInfo);
  }

  abstract protected void listenEvent(T event);
}
