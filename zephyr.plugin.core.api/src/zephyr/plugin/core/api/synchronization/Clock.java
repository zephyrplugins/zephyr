package zephyr.plugin.core.api.synchronization;

import zephyr.plugin.core.api.signals.Signal;


public class Clock {
  public final Signal<Clock> onTick = new Signal<Clock>();
  public final boolean isSuspendable;
  private long timeStep = -1;
  private long lastUpdate = System.nanoTime();
  private long lastPeriod = 0;
  private boolean terminating = false;
  private boolean terminated = false;
  private final ClockInfo info;

  public Clock() {
    this("NoName", true);
  }

  public Clock(String label) {
    this(label, true);
  }

  public Clock(String label, boolean isSuspendable) {
    this.isSuspendable = isSuspendable;
    info = new ClockInfo(label);
  }

  public boolean tick() {
    if (terminated)
      throw new RuntimeException("Clock is terminated");
    if (terminating) {
      terminated = true;
      return false;
    }
    timeStep++;
    updateChrono();
    onTick.fire(this);
    return true;
  }

  private void updateChrono() {
    long currentTime = System.nanoTime();
    lastPeriod = currentTime - lastUpdate;
    lastUpdate = currentTime;
  }

  public long timeStep() {
    return timeStep;
  }

  public long lastPeriodNano() {
    return lastPeriod;
  }

  public void terminate() {
    terminating = true;
  }

  public boolean isTerminated() {
    return terminating;
  }

  public ClockInfo info() {
    return info;
  }
}
