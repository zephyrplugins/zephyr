package zephyr.plugin.core.api.signals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Signal<T> implements Serializable {
  private static final long serialVersionUID = -8879732560976561426L;

  static public interface EventInfoGenerator<T> {
    T generate();
  }

  private final List<Listener<T>> listeners = new LinkedList<Listener<T>>();

  public Signal() {
    super();
  }

  synchronized public Listener<T> connect(Listener<T> listener) {
    assert listener != null;
    assert this.listeners.indexOf(listener) == -1;
    this.listeners.add(listener);
    return listener;
  }

  synchronized public void disconnect(Listener<T> listener) {
    boolean elementFound;
    elementFound = listeners.remove(listener);
    assert elementFound;
  }

  public final void fire(T eventInfo) {
    for (Listener<T> listener : getListeners())
      listener.listen(eventInfo);
  }

  public synchronized ArrayList<Listener<T>> getListeners() {
    return new ArrayList<Listener<T>>(listeners);
  }

  public final void fireIFN(EventInfoGenerator<T> eventInfoGenerator) {
    if (listeners.isEmpty())
      return;
    fire(eventInfoGenerator.generate());
  }

  public int nbListeners() {
    return listeners.size();
  }
}
