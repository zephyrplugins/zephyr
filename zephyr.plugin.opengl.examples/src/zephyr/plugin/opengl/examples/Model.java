package zephyr.plugin.opengl.examples;

import rlpark.plugin.utils.logger.helpers.DataLogged;
import rlpark.plugin.utils.time.Clock;
import rlpark.plugin.utils.time.Timed;
import zephyr.Zephyr;
import zephyr.ZephyrPlotting;

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
