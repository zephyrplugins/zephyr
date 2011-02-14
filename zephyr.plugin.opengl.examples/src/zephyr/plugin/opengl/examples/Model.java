package zephyr.plugin.opengl.examples;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Timed;

public class Model implements Runnable, Timed {

  final Clock clock = new Clock("Model");
  @Monitor
  float insideRadius;

  public Model() {
    Zephyr.advertise(this);
  }

  @Override
  public void run() {
    while (!clock.isTerminated()) {
      clock.tick();
      insideRadius = 1.9f + (float) Math.sin((0.05f * clock.timeStep()));
    }
  }

  @Override
  public Clock clock() {
    return clock;
  }
}
