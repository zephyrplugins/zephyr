package zephyr.plugin.core.internal.clocks;

import zephyr.plugin.core.api.synchronization.Chrono;

public class ClockStat {
  static final double PeriodUpdateRate = 0.9;

  static class PeriodStats {
    private final Chrono chrono = new Chrono();
    private long period = -1;

    public PeriodStats() {
    }

    public void start() {
      chrono.start();
    }

    public void measure() {
      long lastPeriodNano = chrono.getCurrentNano();
      if (period == -1)
        period = lastPeriodNano;
      period = (long) (PeriodUpdateRate * period + (1.0 - PeriodUpdateRate) * lastPeriodNano);
    }

    public long period() {
      return period;
    }
  }

  private final PeriodStats modelPeriod = new PeriodStats();
  private final PeriodStats fullPeriod = new PeriodStats();

  public ClockStat() {
  }

  public void updateBeforeSynchronization() {
    fullPeriod.measure();
    fullPeriod.start();
    modelPeriod.measure();
  }

  public void updateAfterSynchronization() {
    modelPeriod.start();
  }

  public long modelPeriod() {
    return modelPeriod.period();
  }

  public long fullPeriod() {
    return fullPeriod.period();
  }
}
