package zephyr.plugin.core.async.recognizers;

public interface OnEventBlocker {
  void connect();

  void block();
}
