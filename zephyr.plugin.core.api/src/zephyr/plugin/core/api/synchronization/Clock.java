package zephyr.plugin.core.api.synchronization;

import zephyr.plugin.core.api.signals.Signal;


public class Clock {
  static final private double periodUpdateRate = 0.9;

  public final Signal<Clock> onTick = new Signal<Clock>();
  public final boolean isSuspendable;
  private int time = -1;
  private final Chrono chrono = new Chrono();
  private long lastUpdate = chrono.getCurrentMillis();
  private double period = -1;

  public Clock() {
    this(true);
  }

  public Clock(boolean isSuspendable) {
    this.isSuspendable = isSuspendable;
  }

  public void tick() {
    time++;
    updateChrono();
    onTick.fire(this);
  }

  private void updateChrono() {
    long currentTime = chrono.getCurrentMillis();
    if (period < 0)
      period = currentTime - lastUpdate;
    else {
      long timeDifference = currentTime - lastUpdate;
      period = periodUpdateRate * period + (1.0 - periodUpdateRate) * timeDifference;
    }
    lastUpdate = currentTime;
  }

  public int time() {
    return time;
  }

  public double period() {
    return period;
  }

  public boolean hasStarted() {
    return time >= 0;
  }

  public long lastUpdateTime() {
    return lastUpdate;
  }
}
