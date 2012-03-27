package zephyr.plugin.core.internal.async.recognizers;

import zephyr.plugin.core.internal.async.events.Event;

public class RecognizeID implements EventRecognizer {
  private final String id;

  public RecognizeID(String id) {
    this.id = id;
  }

  @Override
  public boolean recognize(Event event) {
    return id.equals(event.id());
  }

}
