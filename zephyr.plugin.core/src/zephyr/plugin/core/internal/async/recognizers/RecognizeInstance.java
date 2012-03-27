package zephyr.plugin.core.internal.async.recognizers;

import zephyr.plugin.core.internal.async.events.Event;

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
