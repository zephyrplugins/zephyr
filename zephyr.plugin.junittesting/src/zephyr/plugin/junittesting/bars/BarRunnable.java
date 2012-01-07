package zephyr.plugin.junittesting.bars;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.annotations.Popup;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class BarRunnable implements Runnable {
  @Popup
  private final BarModel barModel = new BarModel();
  private final Clock clock = new Clock("ModelRunnable");

  public BarRunnable() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    while (clock.tick())
      barModel.update();
  }
}
