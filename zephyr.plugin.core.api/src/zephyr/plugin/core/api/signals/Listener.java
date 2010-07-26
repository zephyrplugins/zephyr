package zephyr.plugin.core.api.signals;

public interface Listener<T> {
  void listen(T eventInfo);
}
