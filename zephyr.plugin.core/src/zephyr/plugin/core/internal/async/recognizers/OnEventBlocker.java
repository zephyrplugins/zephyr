package zephyr.plugin.core.internal.async.recognizers;

public interface OnEventBlocker {
  void connect();

  void block();
}
