package zephyr.plugin.core.api.monitoring.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.fileloggers.FileLogger;
import zephyr.plugin.core.api.monitoring.fileloggers.LoggerRow;
import zephyr.plugin.core.api.monitoring.fileloggers.TimedFileLogger;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.monitoring.wrappers.Wrappers;

public class Loggers {
  static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

  public static String fileNameTimeStamp() {
    return dateFormat.format(new Date());
  }

  public static void add(DataMonitor logger, String[] elementLabels, int level, double[] data) {
    add(logger, elementLabels, level, data, null);
  }

  public static void add(DataMonitor logger, String[] elementLabels, int level,
      final double[] data, List<MonitorWrapper> wrappers) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], level, new Monitored() {
        @Override
        public double monitoredValue() {
          return data[elementIndex];
        }
      }, wrappers);
    }
  }

  public static void add(DataMonitor logger, String[] elementLabels, int level, final int[] data,
      List<MonitorWrapper> wrappers) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], level, new Monitored() {
        @Override
        public double monitoredValue() {
          return data[elementIndex];
        }
      }, wrappers);
    }
  }

  public static void add(DataMonitor logger, String[] elementLabels, int level, final float[] data,
      List<MonitorWrapper> wrappers) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      addMonitored(logger, elementLabels[i], level, new Monitored() {
        @Override
        public double monitoredValue() {
          return data[elementIndex];
        }
      }, wrappers);
    }
  }

  public static void addMonitored(DataMonitor logger, String label, int level, Monitored monitored,
      List<MonitorWrapper> wrappers) {
    logger.add(label, level, monitored);
    if (wrappers == null)
      return;
    for (MonitorWrapper wrapper : wrappers)
      logger.add(label + Wrappers.wrapperLabel(wrapper), level, wrapper.createMonitored(monitored));
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
    return dataLogged.arrayDecoration();
  }

  static public TimedFileLogger newLoggerWithTime(String filepath) {
    return newLoggerWithTime(filepath, false);
  }

  public static FileLogger newLogger(String filepath) {
    try {
      return new FileLogger(filepath);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static LoggerRow newLoggerRow(String filepath) {
    try {
      return new LoggerRow(filepath);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static TimedFileLogger newLoggerWithTime(String filepath, boolean compress) {
    try {
      return new TimedFileLogger(filepath, false);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void copyFile(File sourceFile, File destFile) throws IOException {
    if (!destFile.exists() && !destFile.createNewFile())
      throw new RuntimeException("Error creating the new file: " + destFile.getAbsolutePath());
    FileChannel source = null;
    FileChannel destination = null;
    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    } finally {
      if (source != null)
        source.close();
      if (destination != null)
        destination.close();
    }
  }

  public static void checkParentFolder(String filepath) {
    checkFolder(new File(filepath).getParentFile().getAbsolutePath());
  }

  public static void checkFolder(String folderPath) {
    File folder = new File(folderPath);
    if (!folder.canRead())
      if (!folder.mkdirs())
        throw new RuntimeException("Error creating the folder: " + folder.getAbsolutePath());
  }
}
