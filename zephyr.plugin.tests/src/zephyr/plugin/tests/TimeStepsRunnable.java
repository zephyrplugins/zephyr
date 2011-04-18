package zephyr.plugin.tests;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.synchronization.Clock;

public class TimeStepsRunnable implements Runnable {

  private final Clock clock01 = new Clock("Clock01");
  private final Clock clock02 = new Clock("Clock02");

  public TimeStepsRunnable() {
    ZephyrPlotting.createMonitor(clock01).add("clock01", new Monitored() {
      @Override
      public double monitoredValue(long stepTime) {
        return stepTime;
      }
    });
    ZephyrPlotting.createMonitor(clock02).add("clock02", new Monitored() {
      @Override
      public double monitoredValue(long stepTime) {
        return stepTime;
      }
    });
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
