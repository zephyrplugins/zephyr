package zephyr.plugin.core.api.logging.helpers;

import java.util.List;

import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.logging.abstracts.Monitored;
import zephyr.plugin.core.api.logging.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.logging.wrappers.Wrappers;

public class Loggers {
  public static void add(Logger logger, String[] elementLabels, double[] data) {
    add(logger, elementLabels, data, null);
  }

  public static void add(Logger logger, String[] elementLabels, final double[] data, List<MonitorWrapper> wrappers) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], new Monitored() {
        @Override
        public double loggedValue(long stepTime) {
          return data[elementIndex];
        }
      }, wrappers);
    }
  }

  public static void add(Logger logger, String[] elementLabels, final int[] data, List<MonitorWrapper> wrappers) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], new Monitored() {
        @Override
        public double loggedValue(long stepTime) {
          return data[elementIndex];
        }
      }, wrappers);
    }
  }

  public static void add(Logger logger, String[] elementLabels, final float[] data, List<MonitorWrapper> wrappers) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], new Monitored() {
        @Override
        public double loggedValue(long stepTime) {
          return data[elementIndex];
        }
      }, wrappers);
    }
  }

  public static void addMonitored(Logger logger, String label, Monitored monitored, List<MonitorWrapper> wrappers) {
    logger.add(label, monitored);
    if (wrappers == null)
      return;
    for (MonitorWrapper wrapper : wrappers)
      logger.add(label + Wrappers.wrapperLabel(wrapper), wrapper.createLogged(monitored));
  }
}
