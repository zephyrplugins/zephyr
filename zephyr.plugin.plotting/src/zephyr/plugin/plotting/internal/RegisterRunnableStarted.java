package zephyr.plugin.plotting.internal;

import zephyr.ZephyrPlotting;
import zephyr.ZephyrSync;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Timed;
import zephyr.plugin.core.startup.StartupJob;

public class RegisterRunnableStarted implements StartupJob, Listener<Runnable> {
  final static private String TimedError = "Warning: %s is annotated %s but does not implement %s to provide a clock.";

  @Override
  public int level() {
    return 10;
  }

  @Override
  public void run() {
    ZephyrSync.onRunnableStarted().connect(this);
  }

  @Override
  public void listen(Runnable runnable) {
    if (!runnable.getClass().isAnnotationPresent(Monitor.class))
      return;
    if (!(runnable instanceof Timed)) {
      System.err.println(String.format(TimedError, runnable,
                                       Monitor.class.getSimpleName(),
                                       Timed.class.getSimpleName()));
      return;
    }
    Clock clock = ((Timed) runnable).clock();
    DataMonitor logger = ZephyrPlotting.createLogger(clock);
    logger.add(runnable);
  }
}
