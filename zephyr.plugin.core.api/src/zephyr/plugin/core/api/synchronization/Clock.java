package zephyr.plugin.core.api.synchronization;

import zephyr.plugin.core.api.signals.Signal;


public class Clock {
  public final Signal<Clock> onTick = new Signal<Clock>();
  public final boolean isSuspendable;
  private long timeStep = -1;
  private long lastUpdate = System.nanoTime();
  private long lastPeriod = 0;
  private boolean terminated = false;

  public Clock() {
    this(true);
  }

  public Clock(boolean isSuspendable) {
    this.isSuspendable = isSuspendable;
  }

  public void tick() {
    timeStep++;
    updateChrono();
    onTick.fire(this);
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

  synchronized public void terminate() {
    assert !terminated;
    terminated = true;
    notifyAll();
  }

  public boolean isTerminated() {
    return terminated;
  }
}
