package zephyr.plugin.tests;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.synchronization.Clock;

public class TimeStepsRunnable implements Runnable {

  private final Clock clock01 = new Clock("Clock01");
  private final Clock clock02 = new Clock("Clock02");

  public TimeStepsRunnable() {
    ZephyrPlotting.createLogger(clock01).add("clock01", new Monitored() {
      @Override
      public double loggedValue(long stepTime) {
        return stepTime;
      }
    }, 0);
    ZephyrPlotting.createLogger(clock02).add("clock02", new Monitored() {
      @Override
      public double loggedValue(long stepTime) {
        return stepTime;
      }
    }, 0);
  }

  @Override
  public void run() {
    while (!clock01.isTerminated() && !clock02.isTerminated()) {
      clock01.tick();
      if (clock01.timeStep() % 2 == 0)
        clock02.tick();
    }
  }
}
