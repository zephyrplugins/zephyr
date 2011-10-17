package zephyr.example.simpleplugin;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class SimplePlugin implements Runnable {
  private final double[] a = new double[10];

  @Override
  public void run() {
    Clock clock = new Clock("Simple");
    Zephyr.advertise(clock, this);
    while (clock.tick()) {
      for (int i = 0; i < a.length; i++)
        a[i] = (a[i] + 1) % i;
    }
  }
}
