package zephyr.plugin.plotting.internal;

import rlpark.plugin.utils.Utils;
import rlpark.plugin.utils.events.Listener;
import rlpark.plugin.utils.logger.abstracts.Logger;
import rlpark.plugin.utils.logger.helpers.DataLogged;
import rlpark.plugin.utils.time.Clock;
import rlpark.plugin.utils.time.Timed;
import zephyr.ZephyrPlotting;
import zephyr.plugin.common.ZephyrPluginCommon;
import zephyr.plugin.common.startup.StartupJob;

public class RegisterRunnableStarted implements StartupJob, Listener<Runnable> {
  final static private String TimedError = "Warning: %s is annotated %s but does not implement %s to provide a clock.";

  @Override
  public int level() {
    return 0;
  }

  @Override
  public void run() {
    ZephyrPluginCommon.onRunnableStarted.connect(this);
  }

  @Override
  public void listen(Runnable runnable) {
    if (!runnable.getClass().isAnnotationPresent(DataLogged.class))
      return;
    if (!(runnable instanceof Timed)) {
      System.err.println(String.format(TimedError, runnable,
                                       DataLogged.class.getSimpleName(),
                                       Timed.class.getSimpleName()));
      return;
    }
    Clock clock = ((Timed) runnable).clock();
    String label = Utils.classLabel(runnable);
    Logger logger = ZephyrPlotting.createLogger(label, clock);
    logger.add(runnable);
  }
}
