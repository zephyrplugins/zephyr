package zephyr.plugin.core.api.monitoring.helpers;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.fileloggers.TimedFileLogger;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.monitoring.wrappers.Wrappers;

public class Loggers {
  public static void add(DataMonitor logger, String[] elementLabels, double[] data, int level) {
    add(logger, elementLabels, data, null, level);
  }

  public static void add(DataMonitor logger, String[] elementLabels, final double[] data, List<MonitorWrapper> wrappers, int level) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], new Monitored() {
        @Override
        public double loggedValue(long stepTime) {
          return data[elementIndex];
        }
      }, wrappers, level);
    }
  }

  public static void add(DataMonitor logger, String[] elementLabels, final int[] data, List<MonitorWrapper> wrappers, int level) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], new Monitored() {
        @Override
        public double loggedValue(long stepTime) {
          return data[elementIndex];
        }
      }, wrappers, level);
    }
  }

  public static void add(DataMonitor logger, String[] elementLabels, final float[] data, List<MonitorWrapper> wrappers, int level) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], new Monitored() {
        @Override
        public double loggedValue(long stepTime) {
          return data[elementIndex];
        }
      }, wrappers, level);
    }
  }

  public static void addMonitored(DataMonitor logger, String label, Monitored monitored, List<MonitorWrapper> wrappers, int level) {
    logger.add(label, monitored, level);
    if (wrappers == null)
      return;
    for (MonitorWrapper wrapper : wrappers)
      logger.add(label + Wrappers.wrapperLabel(wrapper), wrapper.createLogged(monitored), 0);
  }

  public static boolean isIndexIncluded(Field field) {
    return isIndexIncluded(field.getAnnotation(Monitor.class));
  }

  public static boolean isIndexIncluded(Class<?> objectClass) {
    return isIndexIncluded(objectClass.getAnnotation(Monitor.class));
  }

  private static boolean isIndexIncluded(Monitor dataLogged) {
    if (dataLogged == null)
      return true;
    return dataLogged.arrayIndexLabeled();
  }

  static public TimedFileLogger newLoggerWithTime(String filepath) {
    try {
      return new TimedFileLogger(filepath);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}