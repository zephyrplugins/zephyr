package zephyr.plugin.core.views.helpers;

import java.util.concurrent.Semaphore;

public class ViewLock {
  private final Semaphore viewLock = new Semaphore(1, true);

  public ViewLock() {
  }

  public boolean acquire() {
    try {
      viewLock.acquire();
    } catch (InterruptedException e) {
      return false;
    }
    return true;
  }

  public void release() {
    viewLock.release();
  }

  public boolean tryAcquire() {
    return viewLock.tryAcquire();
  }
}
