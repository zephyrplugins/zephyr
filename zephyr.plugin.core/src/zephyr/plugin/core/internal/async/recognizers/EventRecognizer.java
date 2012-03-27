package zephyr.plugin.core.internal.async.recognizers;

import zephyr.plugin.core.internal.async.events.Event;

public interface EventRecognizer {
  boolean recognize(Event event);
}
