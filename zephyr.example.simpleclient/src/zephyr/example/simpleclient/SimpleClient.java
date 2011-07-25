package zephyr.example.simpleclient;

import java.util.Random;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class SimpleClient implements Runnable {
  private final Random random = new Random(0);
  /**
   * This array will be monitored by Zephyr because the class is annotated with @Monitor
   */
  private final double[] a = new double[10];
  /**
   * Clock instance used to synchronize with Zephyr
   */
  private final Clock clock = new Clock("SimpleClient");

  public SimpleClient() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    // While the clock has not been terminated we can continue to generate data
    while (!clock.isTerminated()) {
      for (int i = 0; i < a.length; i++)
        a[i] = (a[i] + random.nextDouble() * (i + 1)) % 1000;
      // We call tick() to tell Zephyr that the data can be collected now
      clock.tick();
    }
  }
}
