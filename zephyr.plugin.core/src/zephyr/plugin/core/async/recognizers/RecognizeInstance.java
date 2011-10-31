package zephyr.plugin.core.async.recognizers;

import zephyr.plugin.core.async.events.Event;

public class RecognizeInstance implements EventRecognizer {
  private final Event event;

  public RecognizeInstance(Event event) {
    this.event = event;
  }

  @Override
  public boolean recognize(Event event) {
    return event == this.event;
  }

}
