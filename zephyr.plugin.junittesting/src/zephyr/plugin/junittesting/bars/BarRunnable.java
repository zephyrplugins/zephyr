package zephyr.plugin.junittesting.bars;

import java.util.Random;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

public class BarRunnable implements Runnable {
  @Monitor
  private final float[] values = new float[100];
  private final Random random = new Random(0);
  private final Clock clock = new Clock("ModelRunnable");

  public BarRunnable() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    while (clock.tick())
      for (int i = 0; i < values.length; i++)
        values[i] += random.nextInt(3) - 1;
  }

  public float[] data() {
    return values;
  }
}
