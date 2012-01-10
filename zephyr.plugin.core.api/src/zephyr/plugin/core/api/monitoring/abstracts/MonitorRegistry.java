package zephyr.plugin.core.api.monitoring.abstracts;

import java.util.ArrayList;
import java.util.List;

import zephyr.plugin.core.api.synchronization.Clock;

public class MonitorRegistry {
  static class MonitorCollection implements DataMonitor {
    private final List<DataMonitor> monitors;

    MonitorCollection(List<DataMonitor> monitors) {
      this.monitors = monitors;
    }

    @Override
    public void add(String label, Monitored monitored) {
      for (DataMonitor monitor : monitors)
        monitor.add(label, monitored);
    }
  }

  static private List<MonitorSynchronizer> factories = new ArrayList<MonitorSynchronizer>();

  static public void registerFactory(MonitorSynchronizer monitorFactory) {
    factories.add(monitorFactory);
  }

  static public void unregisterFactory(MonitorSynchronizer monitorFactory) {
    factories.remove(monitorFactory);
  }

  static public DataMonitor getSynchronizedMonitor(Clock clock) {
    List<DataMonitor> monitors = new ArrayList<DataMonitor>();
    for (MonitorSynchronizer factory : factories)
      monitors.add(factory.getSyncMonitor(clock));
    return new MonitorCollection(monitors);
  }
}
