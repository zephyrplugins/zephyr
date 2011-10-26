package zephyr.plugin.core.internal.async;

import java.util.concurrent.Semaphore;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.async.BusEvent;
import zephyr.plugin.core.async.Event;
import zephyr.plugin.core.async.EventListener;
import zephyr.plugin.core.async.ListenerRecorder;


public class ZephyrBusEvent implements BusEvent {
  final PendingEvents pendingEvents = new PendingEvents();
  final RegisteredListeners registeredListeners = new RegisteredListeners();
  private final EventProcessor processor = new EventProcessor(pendingEvents, registeredListeners);

  public ZephyrBusEvent() {
  }

  @Override
  public void dispatch(Event event) {
    pendingEvents.dispatch(event);
  }

  @Override
  public void register(String id, EventListener listener) {
    registeredListeners.register(id, listener);
  }

  @Override
  public void unregister(String id, EventListener listener) {
    registeredListeners.unregister(id, listener);
  }

  @Override
  public void register(Event event, EventListener listener) {
    registeredListeners.register(event, listener);
  }

  @Override
  public void unregister(Event event, EventListener listener) {
    registeredListeners.unregister(event, listener);
  }

  @Override
  public void syncDispatch(final Event event) {
    final Semaphore semaphore = new Semaphore(0);
    Listener<Event> listener = new Listener<Event>() {
      @Override
      public void listen(Event eventProcessed) {
        if (event == eventProcessed)
          semaphore.release();
      }
    };
    processor.onEventProcessed.connect(listener);
    dispatch(event);
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    processor.onEventProcessed.disconnect(listener);
  }

  public void registerRecorders() {
    IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("zephyr.listener");
    for (IConfigurationElement element : config) {
      try {
        Object o = element.createExecutableExtension("class");
        if (!(o instanceof ListenerRecorder)) {
          System.err.println(o.getClass().getSimpleName() + " does not implement " + ListenerRecorder.class.getName());
          continue;
        }
        ((ListenerRecorder) o).recordListener(this);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }

  public void start() {
    Thread thread = new Thread(processor);
    thread.setDaemon(true);
    thread.setName("Zephyr Bus Event");
    thread.start();
  }
}
