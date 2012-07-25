package zephyr.plugin.core.privates.clocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.events.ClockEvent;

public class Clocks {
  private final Map<Clock, ClockStat> clocks = Collections.synchronizedMap(new LinkedHashMap<Clock, ClockStat>());

  public Clocks() {
  }

  public void register(Clock clock) {
    if (clocks.containsKey(clock))
      return;
    clocks.put(clock, new ClockStat());
    ZephyrSync.busEvent().dispatch(new ClockEvent(ClockEvent.AddedID, clock));
  }

  public void remove(Clock clock) {
    if (!clocks.containsKey(clock))
      return;
    clocks.remove(clock);
    ZephyrSync.busEvent().dispatch(new ClockEvent(ClockEvent.RemovedID, clock));
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
