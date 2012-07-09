package zephyr.plugin.core.privates.synchronization.binding;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.eclipse.jface.preference.IPreferenceStore;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.privates.ZephyrPluginCore;
import zephyr.plugin.core.privates.clocks.ClockStat;

public class SynchronizationMode {
  public static final String SynchronousMode = "zephyr.synchronizationmode";
  public static final String SynchronousDelay = "zephyr.synchronizationdelay";

  public enum Mode {
    Delayed, Asynchrone, Synchrone
  }

  private long delay = 0;
  private Mode mode = Mode.Synchrone;
  private final Map<Clock, ClockStat> chronos = new LinkedHashMap<Clock, ClockStat>();

  private void delayClock(Clock clock) {
    ClockStat clockStat = chronos.get(clock);
    long clockDelay = delay - (clockStat.modelPeriod() / 1000000);
    if (clockDelay >= 1)
      try {
        Thread.sleep(clockDelay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
  }

  private static void waitCompletion(List<Future<?>> futures) {
    for (Future<?> future : futures)
      try {
        future.get();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
  }

  public void synchronize(Clock clock, List<Future<?>> futures) {
    switch (mode) {
    case Asynchrone:
      return;
    case Delayed:
      delayClock(clock);
      return;
    case Synchrone:
      waitCompletion(futures);
      return;
    }
  }

  public void setMode(Mode mode, long delay) {
    this.mode = mode;
    this.delay = delay;
    IPreferenceStore preferenceStore = ZephyrPluginCore.getDefault().getPreferenceStore();
    preferenceStore.setValue(SynchronousMode, mode.ordinal());
    preferenceStore.setValue(SynchronousDelay, delay);
  }

  public void addClock(Clock clock, ClockStat clockStat) {
    chronos.put(clock, clockStat);
  }

  public void removeClock(Clock clock) {
    chronos.remove(clock);
  }
}
