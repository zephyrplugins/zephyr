package zephyr.plugin.core.privates.async;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import zephyr.plugin.core.internal.async.BusEvent;
import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.EventListener;
import zephyr.plugin.core.internal.async.recognizers.EventRecognizer;
import zephyr.plugin.core.internal.async.recognizers.OnEventBlocker;
import zephyr.plugin.core.internal.async.recognizers.RecognizeInstance;


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

  public void registerRecorders() {
    IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("zephyr.listener");
    for (IConfigurationElement element : config) {
      try {
        Object o = element.createExecutableExtension("class");
        if (!(o instanceof EventListener)) {
          System.err.println(o.getClass().getSimpleName() + " does not implement " + EventListener.class.getName());
          continue;
        }
        String eventID = element.getAttribute("eventid");
        if (eventID == null) {
          System.err.println(o.getClass().getSimpleName() + " does not implement " + EventListener.class.getName());
          continue;
        }
        register(eventID, (EventListener) o);
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

  @Override
  public void syncDispatch(final Event event) {
    OnEventBlocker reference = createWaiter(new RecognizeInstance(event));
    reference.connect();
    dispatch(event);
    reference.block();
  }

  @Override
  public OnEventBlocker createWaiter(EventRecognizer recognizer) {
    return new ZephyrRecognizerReference(this, recognizer);
  }

  public EventProcessor processor() {
    return processor;
  }
}
