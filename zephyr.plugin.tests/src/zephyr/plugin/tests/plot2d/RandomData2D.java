package zephyr.plugin.tests.plot2d;

import java.util.Random;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Timed;

public class RandomData2D implements Runnable, Timed {
  @Monitor
  protected final float[] data = new float[3];
  private final Clock clock = new Clock("Random2D");
  private final Random random = new Random(0);

  public RandomData2D() {
    Zephyr.advertise(this);
  }

  @Override
  public void run() {
    while (!clock.isTerminated()) {
      for (int i = 0; i < data.length; i++)
        data[i] += (random.nextFloat() - 0.5) * i;
      clock.tick();
    }
  }

  @Override
  public Clock clock() {
    return clock;
  }
}
