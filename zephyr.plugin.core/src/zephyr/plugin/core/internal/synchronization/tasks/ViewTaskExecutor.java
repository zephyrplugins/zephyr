package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ViewTaskExecutor extends ThreadPoolExecutor {
  static protected ThreadGroup viewThreadGroup = new ThreadGroup("ZephyrViews");
  static private int NbThread = 1;

  static protected class Factory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(viewThreadGroup, runnable);
      thread.setName("ZephyrView" + viewThreadGroup.activeCount());
      thread.setPriority(Thread.MIN_PRIORITY);
      thread.setDaemon(true);
      return thread;
    }
  }

  public ViewTaskExecutor() {
    super(NbThread, NbThread, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new Factory());
  }

  @Override
  public Future<?> submit(Runnable task) {
    return super.submit(task);
  }

  @Override
  synchronized protected void afterExecute(Runnable r, Throwable t) {
    ViewTaskScheduler.onTaskExecuted.fire(this);
  }
}
