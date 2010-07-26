package zephyr.plugin.core.api.logging.fileloggers;

import java.io.FileNotFoundException;
import java.io.StringWriter;

import zephyr.plugin.core.api.synchronization.Clock;

public class TimedFileLogger extends FileLogger {
  private final Clock clock = new Clock();

  public TimedFileLogger(String filepath) throws FileNotFoundException {
    super(filepath, false, false);
  }

  public TimedFileLogger(String filepath, boolean timeStamp, boolean temporaryFile) throws FileNotFoundException {
    super(filepath, timeStamp, temporaryFile);
  }

  public TimedFileLogger(StringWriter writer) {
    super(writer, false);
  }

  public void update() {
    clock.tick();
    super.update(clock.time());
  }
}
