package zephyr.plugin.opengl.examples;

import zephyr.Zephyr;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.monitoring.DataLogged;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Timed;

public class Model implements Runnable, Timed {

  final Clock clock = new Clock();
  @DataLogged
  float insideRadius;

  public Model() {
    Zephyr.advertize(clock, this);
    ZephyrPlotting.createLogger("Model", clock).add(this);
  }

  @Override
  public void run() {
    while (true) {
      clock.tick();
      insideRadius = 1.9f + ((float) Math.sin((0.004f * clock.time())));
    }
  }

  @Override
  public Clock clock() {
    return clock;
  }
}
