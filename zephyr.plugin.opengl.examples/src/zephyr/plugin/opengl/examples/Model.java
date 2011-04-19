package zephyr.plugin.opengl.examples;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

public class Model implements Runnable {

  final Clock clock = new Clock("Model");
  @Monitor
  float insideRadius;

  public Model() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    while (!clock.isTerminated()) {
      clock.tick();
      insideRadius = 1.9f + (float) Math.sin((0.05f * clock.timeStep()));
    }
  }
}
