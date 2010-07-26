package zephyr.plugin.tests;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.logging.abstracts.Logged;
import zephyr.plugin.core.api.synchronization.Clock;

public class TimeStepsRunnable implements Runnable {

  private final Clock clock01 = new Clock();
  private final Clock clock02 = new Clock();

  public TimeStepsRunnable() {
    ZephyrPlotting.createLogger("Clock01", clock01).add("clock01", new Logged() {
      @Override
      public double loggedValue(long stepTime) {
        return stepTime;
      }
    });
    ZephyrPlotting.createLogger("Clock02", clock02).add("clock02", new Logged() {
      @Override
      public double loggedValue(long stepTime) {
        return stepTime;
      }
    });
  }

  @Override
  public void run() {
    while (true) {
      clock01.tick();
      if (clock01.time() % 2 == 0)
        clock02.tick();
    }
  }
}
