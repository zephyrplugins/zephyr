package zephyr.plugin.core.api.monitoring.fileloggers;

import java.io.FileNotFoundException;
import java.io.StringWriter;

public class TimedFileLogger extends FileLogger {
  public TimedFileLogger(String filepath) throws FileNotFoundException {
    this(filepath, false);
  }

  public TimedFileLogger(StringWriter writer) {
    super(writer, false);
  }

  public TimedFileLogger(String filepath, boolean temporaryFile) throws FileNotFoundException {
    super(filepath, true, temporaryFile);
  }

  public void update() {
    update(System.currentTimeMillis());
  }
}
