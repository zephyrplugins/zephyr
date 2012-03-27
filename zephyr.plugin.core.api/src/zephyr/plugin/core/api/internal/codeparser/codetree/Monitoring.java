package zephyr.plugin.core.api.internal.codeparser.codetree;

import java.util.List;

import zephyr.plugin.core.api.internal.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Wrappers;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class Monitoring {
  public static void addMonitored(DataMonitor logger, String label, Monitored monitored, List<MonitorWrapper> wrappers,
      int level) {
    logger.add(label, monitored);
    if (wrappers == null)
      return;
    for (MonitorWrapper wrapper : wrappers)
      logger.add(label + Wrappers.wrapperLabel(wrapper), wrapper.createMonitored(monitored));
  }

  public static void add(DataMonitor logger, String[] elementLabels, final double[] data,
      List<MonitorWrapper> wrappers, int level) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], new Monitored() {
        @Override
        public double monitoredValue() {
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
        public double monitoredValue() {
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
        public double monitoredValue() {
          return data[elementIndex];
        }
      }, wrappers, level);
    }
  }
}
