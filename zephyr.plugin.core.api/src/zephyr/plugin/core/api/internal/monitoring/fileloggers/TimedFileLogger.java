package zephyr.plugin.core.api.internal.monitoring.fileloggers;

import java.io.IOException;
import java.io.StringWriter;

public class TimedFileLogger extends FileLogger {
  public TimedFileLogger(String filepath) throws IOException {
    this(filepath, false);
  }

  public TimedFileLogger(StringWriter writer) {
    super(writer, false);
  }

  public TimedFileLogger(String filepath, boolean temporaryFile) throws IOException {
    super(filepath, true, temporaryFile);
  }

  public void update() {
    update(System.currentTimeMillis());
  }
}
