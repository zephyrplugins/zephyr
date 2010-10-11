package zephyr.plugin.plotting.internal;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.ZephyrPluginCommon;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.monitoring.Monitor;
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
    ZephyrPluginCommon.onRunnableStarted.connect(this);
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
    String label = Labels.classLabel(runnable);
    Logger logger = ZephyrPlotting.createLogger(label, clock);
    logger.add(runnable);
  }
}
