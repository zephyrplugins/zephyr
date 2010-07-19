package zephyr.plugin.tests.plot2d;

import java.util.Random;

import rlpark.plugin.utils.time.Clock;
import zephyr.Zephyr;

public class RandomData2D implements Runnable {
  protected final double[] data = new double[3];
  private final Clock clock = new Clock();
  private final Random random = new Random(0);

  public RandomData2D() {
    Zephyr.advertize(clock, this);
  }

  @Override
  public void run() {
    while (true) {
      for (int i = 0; i < data.length; i++)
        data[i] += (random.nextDouble() - 0.5) * i;
      clock.tick();
    }
  }
}
