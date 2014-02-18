package zephyr.plugin.core.privates;

import java.util.LinkedHashMap;
import java.util.Map;
import zephyr.plugin.core.RunnableFactory;

public class ThreadStarter {
  private final Map<Thread, RunnableFactory> threadToFactory = new LinkedHashMap<Thread, RunnableFactory>();

  synchronized public void register(RunnableFactory runnableFactory, Thread runnableThread) {
    threadToFactory.put(runnableThread, runnableFactory);
  }

  synchronized public RunnableFactory factory(Thread runnableThread) {
    return threadToFactory.get(runnableThread);
  }
}
