package zephyr.plugin.tests.plot2d;

import java.util.Random;

import zephyr.ZephyrCore;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

public class RandomData2D implements Runnable {
  @Monitor
  protected final float[] data = new float[3];
  private final Clock clock = new Clock();
  private final Random random = new Random(0);

  public RandomData2D() {
    ZephyrPlotting.createLogger("Random", clock).add(this);
    ZephyrCore.advertize(clock, this);
  }

  @Override
  public void run() {
    while (true) {
      for (int i = 0; i < data.length; i++)
        data[i] += (random.nextFloat() - 0.5) * i;
      clock.tick();
    }
  }
}
