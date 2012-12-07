package zephyr.plugin.core.privates.synchronization;

import java.util.concurrent.Semaphore;

public class Syncins<T> {
  public class Handle {
    private T h = instance;

    public Handle() {
    }

    public T h() {
      return h;
    }

    public void release() {
      h = null;
      lock.release();
    }
  }

  final Semaphore lock = new Semaphore(1, true);
  final T instance;

  public Syncins(T instance) {
    this.instance = instance;
  }

  public Handle acquire() {
    try {
      lock.acquire();
      return new Handle();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Handle tryAcquire() {
    return lock.tryAcquire() ? new Handle() : null;
  }

  public T instance() {
    return instance;
  }
}
