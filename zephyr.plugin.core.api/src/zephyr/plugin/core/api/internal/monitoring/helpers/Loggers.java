package zephyr.plugin.core.api.internal.monitoring.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import zephyr.plugin.core.api.internal.monitoring.fileloggers.FileLogger;
import zephyr.plugin.core.api.internal.monitoring.fileloggers.LoggerRow;
import zephyr.plugin.core.api.internal.monitoring.fileloggers.TimedFileLogger;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class Loggers {
  static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

  public static String fileNameTimeStamp() {
    synchronized (dateFormat) {
      return dateFormat.format(new Date());
    }
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
    FileInputStream source = null;
    FileOutputStream destination = null;
    try {
      source = new FileInputStream(sourceFile);
      destination = new FileOutputStream(destFile);
      destination.getChannel().transferFrom(source.getChannel(), 0, source.getChannel().size());
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
