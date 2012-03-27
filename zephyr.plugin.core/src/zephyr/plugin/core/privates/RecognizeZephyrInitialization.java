package zephyr.plugin.core.privates;

import zephyr.plugin.core.api.Zephyr.AdvertisementInfo;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.recognizers.EventRecognizer;
import zephyr.plugin.core.internal.events.ClockEvent;
import zephyr.plugin.core.internal.events.CodeStructureEvent;

public class RecognizeZephyrInitialization implements EventRecognizer {
  private boolean clockHasBeenAdded;
  private boolean codeHasBeenParsed;
  private final Clock clock;

  public RecognizeZephyrInitialization(AdvertisementInfo advertisementInfo) {
    this.clock = advertisementInfo.clock;
    clockHasBeenAdded = ZephyrPluginCore.clocks().hasClock(clock);
    codeHasBeenParsed = advertisementInfo.advertised == null;
  }

  @Override
  public boolean recognize(Event event) {
    if (CodeStructureEvent.ParsedID.equals(event.id()) && ((CodeStructureEvent) event).clock() == clock)
      codeHasBeenParsed = true;
    if (ClockEvent.AddedID.equals(event.id()) && ((ClockEvent) event).clock() == clock)
      clockHasBeenAdded = true;
    return isSatisfied();
  }

  public boolean isSatisfied() {
    return clockHasBeenAdded && codeHasBeenParsed;
  }
}
