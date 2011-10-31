package zephyr.plugin.core.async.recognizers;

import zephyr.plugin.core.async.events.Event;

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
