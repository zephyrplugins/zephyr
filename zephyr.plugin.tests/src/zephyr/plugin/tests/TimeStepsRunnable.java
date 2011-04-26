package zephyr.plugin.tests;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;

public class TimeStepsRunnable implements Runnable {

  private final Clock clock01 = new Clock("Clock01");
  private final Clock clock02 = new Clock("Clock02");
  long stepTime01;
  long stepTime02;

  public TimeStepsRunnable() {
    Zephyr.advertise(clock01, this);
    Zephyr.advertise(clock02, this);
  }

  @Override
  public void run() {
    while (!clock01.isTerminated() && !clock02.isTerminated()) {
      clock01.tick();
      if (clock01.timeStep() % 2 == 0)
        clock02.tick();
      stepTime01 = clock01.timeStep();
      stepTime02 = clock02.timeStep();
    }
  }
}
