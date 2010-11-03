package zephyr.plugin.opengl.examples;

import zephyr.ZephyrCore;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Timed;

public class Model implements Runnable, Timed {

  final Clock clock = new Clock();
  @Monitor
  float insideRadius;

  public Model() {
    ZephyrCore.advertize(clock, this);
    ZephyrPlotting.createLogger("Model", clock).add(this);
  }

  @Override
  public void run() {
    while (true) {
      clock.tick();
      insideRadius = 1.9f + ((float) Math.sin((0.05f * clock.timeStep())));
    }
  }

  @Override
  public Clock clock() {
    return clock;
  }
}
