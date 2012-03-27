package zephyr.plugin.core.api.internal.monitoring.abstracts;

import java.util.ArrayList;
import java.util.List;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class MonitorRegistry {
  public static class MonitorCollection implements DataMonitor {
    private final List<DataMonitor> monitors;

    public MonitorCollection(List<DataMonitor> monitors) {
      this.monitors = monitors;
    }

    @Override
    public void add(String label, Monitored monitored) {
      for (DataMonitor monitor : monitors)
        monitor.add(label, monitored);
    }
  }

  public static List<MonitorSynchronizer> factories = new ArrayList<MonitorSynchronizer>();

  static public void registerFactory(MonitorSynchronizer monitorFactory) {
    factories.add(monitorFactory);
  }

  static public void unregisterFactory(MonitorSynchronizer monitorFactory) {
    factories.remove(monitorFactory);
  }
}
