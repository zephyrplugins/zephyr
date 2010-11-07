package zephyr.plugin.core.internal.synchronization.tasks;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ViewTaskExecutor extends ThreadPoolExecutor {
  static protected class Factory implements ThreadFactory {
    private final int priority;
    private final ThreadGroup threadGroup = new ThreadGroup("ZephyrViews");

    protected Factory(int priority) {
      this.priority = priority;
    }

    @Override
    public Thread newThread(Runnable runnable) {
      assert threadGroup != null;
      Thread result = new Thread(threadGroup, runnable);
      result.setName("ZephyrView" + threadGroup.activeCount());
      result.setPriority(priority);
      return result;
    }
  }

  private ThreadGroup viewThreadGroup;

  public ViewTaskExecutor(int priority) {
    super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new Factory(priority));
  }

  @Override
  synchronized protected void afterExecute(Runnable r, Throwable t) {
    ViewTaskScheduler.onTaskExecuted.fire(this);
  }

  public void setMasterThreadGroup(ThreadGroup viewThreadGroup) {
    this.viewThreadGroup = viewThreadGroup;
  }

  public int nbActiveZephyrThread() {
    return viewThreadGroup.activeCount();
  }
}
