package zephyr.plugin.junittesting.bars;

import java.util.Random;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class BarModel {
  private final Random random = new Random(0);
  private final double[] values = new double[100];

  public void update() {
    for (int i = 0; i < values.length; i++)
      values[i] += random.nextInt(3) - 1;
  }

  public double[] data() {
    return values;
  }
}
