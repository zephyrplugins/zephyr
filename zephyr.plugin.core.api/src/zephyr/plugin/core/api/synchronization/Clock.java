package zephyr.plugin.core.api.synchronization;

import java.io.Serializable;
import java.util.concurrent.Semaphore;
import zephyr.plugin.core.api.signals.Signal;

public class Clock implements Serializable {
  private static final long serialVersionUID = -1155346148292134613L;
  static private boolean enableDataLock = false;
  public final transient Signal<Clock> onTick = new Signal<Clock>();
  public final transient Signal<Clock> onTerminate = new Signal<Clock>();
  private long timeStep = -1;
  private long lastUpdate = System.nanoTime();
  private long lastPeriod = 0;
  private boolean terminating = false;
  private boolean terminated = false;
  private final ClockInfo info;
  private final Semaphore dataLock = enableDataLock ? new Semaphore(0, true) : null;

  public Clock() {
    this("NoName");
  }

  public Clock(String label) {
    this(label, true, true);
  }

  public Clock(String label, boolean isSuspendable, boolean isTerminable) {
    info = new ClockInfo(label, isSuspendable, isTerminable);
  }

  public boolean tick() {
    releaseData();
    if (terminated)
      throw new RuntimeException("Clock is terminated");
    if (terminating) {
      terminated = true;
      onTerminate.fire(this);
      return false;
    }
    timeStep++;
    updateChrono();
    onTick.fire(this);
    return acquireData();
  }

  public void releaseData() {
    if (!enableDataLock)
      return;
    assert dataLock.availablePermits() == 0;
    dataLock.release();
  }

  public boolean acquireData() {
    if (!enableDataLock)
      return true;
    try {
      dataLock.acquire();
    } catch (InterruptedException e) {
      prepareTermination();
      return false;
    }
    assert dataLock.availablePermits() == 0;
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

  public void prepareTermination() {
    terminating = true;
  }

  public void terminate() {
    if (terminated)
      return;
    prepareTermination();
    tick();
  }

  public boolean isTerminated() {
    return terminating;
  }

  public ClockInfo info() {
    return info;
  }

  static public void setEnableDataLock(boolean enabled) {
    enableDataLock = enabled;
  }
}
