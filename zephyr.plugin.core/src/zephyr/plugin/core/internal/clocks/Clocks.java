package zephyr.plugin.core.internal.clocks;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.events.ClockEvent;
import zephyr.plugin.core.internal.ZephyrPluginCore;

public class Clocks {
  private final Set<Clock> clocks = new LinkedHashSet<Clock>();

  public Clocks() {
  }

  public void register(Clock clock) {
    boolean added;
    synchronized (clocks) {
      added = clocks.add(clock);
    }
    if (added)
      ZephyrCore.busEvent().dispatch(new ClockEvent(ClockEvent.AddedID, clock));
  }

  public void remove(Clock clock) {
    boolean removed;
    synchronized (clocks) {
      removed = clocks.remove(clock);
    }
    if (removed)
      ZephyrCore.busEvent().dispatch(new ClockEvent(ClockEvent.RemovedID, clock));
  }

  public List<Clock> getClocks() {
    List<Clock> result = new ArrayList<Clock>();
    synchronized (clocks) {
      result.addAll(clocks);
    }
    return result;
  }

  public boolean hasClock(Clock clock) {
    boolean result;
    synchronized (clocks) {
      result = clocks.contains(clock);
    }
    return result;
  }

  public ClockStat clockStats(Clock clock) {
    return ZephyrPluginCore.viewBinder().clockViews(clock).clockStats();
  }
}
