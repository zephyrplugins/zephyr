package zephyr.plugin.core.api.codeparser.codetree;

import java.util.List;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.monitoring.wrappers.Wrappers;

public class Monitoring {
  public static void addMonitored(DataMonitor logger, String label, Monitored monitored, List<MonitorWrapper> wrappers,
      int level) {
    logger.add(label, level, monitored);
    if (wrappers == null)
      return;
    for (MonitorWrapper wrapper : wrappers)
      logger.add(label + Wrappers.wrapperLabel(wrapper), level, wrapper.createMonitored(monitored));
  }

  public static void add(DataMonitor logger, String[] elementLabels, final double[] data,
      List<MonitorWrapper> wrappers, int level) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], new Monitored() {
        @Override
        public double monitoredValue(long stepTime) {
          return data[elementIndex];
        }
      }, wrappers, level);
    }
  }

  public static void add(DataMonitor logger, String[] elementLabels, final int[] data, List<MonitorWrapper> wrappers,
      int level) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], new Monitored() {
        @Override
        public double monitoredValue(long stepTime) {
          return data[elementIndex];
        }
      }, wrappers, level);
    }
  }

  public static void add(DataMonitor logger, String[] elementLabels, final float[] data, List<MonitorWrapper> wrappers,
      int level) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], new Monitored() {
        @Override
        public double monitoredValue(long stepTime) {
          return data[elementIndex];
        }
      }, wrappers, level);
    }
  }
}
