package zephyr.example.simpleclient;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class SimpleClient {
  private final double[] a = new double[10];

  public void run() {
    Clock clock = new Clock("Simple");
    Zephyr.advertise(clock, this);
    while (clock.tick()) {
      for (int i = 0; i < a.length; i++)
        a[i] = (a[i] + 1) % i;
    }
  }

  public static void main(String[] args) {
    new SimpleClient().run();
  }
}
