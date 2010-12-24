package zephyr.plugin.tests.slowdrawing;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.ZephyrRunnable;
import zephyr.plugin.core.api.synchronization.Clock;

public class SlowDrawingRunnable implements ZephyrRunnable {
  private final Clock clock = new Clock("SlowDrawing");

  public SlowDrawingRunnable() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    while (!clock.isTerminated())
      clock.tick();
  }

  @Override
  public Clock clock() {
    return clock;
  }
}
