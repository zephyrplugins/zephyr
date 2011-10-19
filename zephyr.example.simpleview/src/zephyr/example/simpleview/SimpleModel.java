package zephyr.example.simpleview;

import java.util.Random;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

public class SimpleModel implements Runnable {
  @Monitor
  protected final float[] data = new float[50];
  private final Clock clock = new Clock("SimpleView");
  private final Random random = new Random(0);

  public SimpleModel() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    while (!clock.isTerminated()) {
      for (int i = 0; i < data.length; i++)
        data[i] += random.nextFloat() - 0.5;
      clock.tick();
    }
  }
}
