package zephyr.plugin.core.api.synchronization;

import zephyr.plugin.core.api.signals.Signal;

public class ClockKillable extends Clock {
  public final Signal<Clock> onKill = new Signal<Clock>();
  private final Closeable closeable;
  private boolean killed = false;
  private boolean dead = false;
  private boolean terminated = false;

  public ClockKillable(Closeable closeable) {
    this(closeable, true);
  }

  public ClockKillable(Closeable closeable, boolean isSuspendable) {
    super(isSuspendable);
    this.closeable = closeable;
  }

  @Override
  public void tick() {
    if (dead)
      return;
    if (killed) {
      close();
      return;
    }
    super.tick();
  }

  synchronized private void close() {
    killed = true;
    onKill.fire(this);
    if (!terminated)
      closeable.close();
    dead = true;
  }

  public void terminate() {
    terminated = true;
  }

  public void kill() {
    killed = true;
    if (terminated)
      close();
  }

  public boolean isKilled() {
    return killed;
  }
}
