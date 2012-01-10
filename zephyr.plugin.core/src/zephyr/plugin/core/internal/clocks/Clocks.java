package zephyr.plugin.core.internal.clocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.events.ClockEvent;

public class Clocks {
  private final Map<Clock, ClockStat> clocks = Collections.synchronizedMap(new LinkedHashMap<Clock, ClockStat>());

  public Clocks() {
  }

  public void register(Clock clock) {
    if (clocks.containsKey(clock))
      return;
    clocks.put(clock, new ClockStat());
    ZephyrCore.busEvent().dispatch(new ClockEvent(ClockEvent.AddedID, clock));
  }

  public void remove(Clock clock) {
    if (!clocks.containsKey(clock))
      return;
    clocks.remove(clock);
    ZephyrCore.busEvent().dispatch(new ClockEvent(ClockEvent.RemovedID, clock));
  }

  public List<Clock> getClocks() {
    return new ArrayList<Clock>(clocks.keySet());
  }

  public boolean hasClock(Clock clock) {
    return clocks.containsKey(clock);
  }

  public ClockStat clockStats(Clock clock) {
    return clocks.get(clock);
  }
}
