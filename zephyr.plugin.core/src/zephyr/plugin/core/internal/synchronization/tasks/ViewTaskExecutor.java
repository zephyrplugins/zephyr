package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ViewTaskExecutor extends ThreadPoolExecutor {
  static protected class Factory implements ThreadFactory {
    private final int priority;

    protected Factory(int priority) {
      this.priority = priority;
    }

    @Override
    public Thread newThread(Runnable runnable) {
      Thread result = Executors.defaultThreadFactory().newThread(runnable);
      result.setPriority(priority);
      return result;
    }
  }

  public ViewTaskExecutor(int nThreads, int priority) {
    super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new Factory(priority));
  }

  @Override
  synchronized protected void afterExecute(Runnable r, Throwable t) {
    ViewTaskScheduler.onTaskExecuted.fire(this);
  }
}
