package zephyr.plugin.tests;

import rlpark.plugin.utils.logger.abstracts.Logged;
import rlpark.plugin.utils.time.Clock;
import zephyr.ZephyrPlotting;

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
