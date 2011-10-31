package zephyr.plugin.core.async;

public interface EventWaiter {
  void connect();

  void waitForEvent();
}
