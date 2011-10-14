package zephyr.example.simpleclient;

import java.util.Random;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;

public class SimpleClient {
  /**
   * This array will be monitored by Zephyr
   */
  private final double[] a = new double[10];
  private final Random random = new Random();

  public void run() {
    // Clock to synchronize between this and Zephyr
    Clock clock = new Clock("Simple");
    // Advertise this with Zephyr and bind this data to the clock
    Zephyr.advertise(clock, this);
    // While we can synchronize data do:
    while (clock.tick()) {
      // Change the data in the array arbitrarily
      for (int i = 0; i < a.length; i++)
        a[i] = (a[i] + random.nextDouble() * (i + 1)) % 1000;
    }
  }

  public static void main(String[] args) {
    new SimpleClient().run();
  }
}
