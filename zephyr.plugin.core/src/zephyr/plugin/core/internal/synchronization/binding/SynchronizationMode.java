package zephyr.plugin.core.internal.synchronization.binding;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.jface.preference.IPreferenceStore;

import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;
import zephyr.plugin.core.internal.ZephyrPluginCore;

public class SynchronizationMode {
  private static final String SynchronousMode = "zephyr.synchronizationmode";
  private static final String SynchronousDelay = "zephyr.synchronizationdelay";

  static public class SynchronizationSettingLoader implements EventListener {
    @Override
    public void listen(Event eventInfo) {
      IPreferenceStore preferenceStore = ZephyrPluginCore.getDefault().getPreferenceStore();
      preferenceStore.setDefault(SynchronousMode, Mode.Asynchrone.ordinal());
      Mode mode = Mode.values()[preferenceStore.getInt(SynchronousMode)];
      long delay = preferenceStore.getLong(SynchronousDelay);
      ClockViews.synchronizationMode.setMode(mode, delay);
    }
  }

  public enum Mode {
    Delayed, Asynchrone, Synchrone
  }

  private long delay = 0;
  private Mode mode = Mode.Synchrone;
  private final Map<Clock, Chrono> chronos = new LinkedHashMap<Clock, Chrono>();

  private void delayClock(Clock clock) {
    Chrono chrono = chronos.get(clock);
    if (chrono == null) {
      chrono = new Chrono();
      chronos.put(clock, chrono);
    }
    long clockDelay = delay - chrono.getCurrentMillis();
    if (clockDelay >= 1)
      try {
        Thread.sleep(clockDelay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    chrono.start();
  }

  private void waitCompletion(List<Future<?>> futures) {
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

  public void addClock(Clock clock) {
  }

  public void removeClock(Clock clock) {
    chronos.remove(clock);
  }
}
