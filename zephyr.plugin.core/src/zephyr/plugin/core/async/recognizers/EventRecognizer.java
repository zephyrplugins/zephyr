package zephyr.plugin.core.async.recognizers;

import zephyr.plugin.core.async.events.Event;

public interface EventRecognizer {
  boolean recognize(Event event);
}
