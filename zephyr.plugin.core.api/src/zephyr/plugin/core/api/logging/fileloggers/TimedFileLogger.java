package zephyr.plugin.core.api.logging.fileloggers;

import java.io.FileNotFoundException;
import java.io.StringWriter;

import zephyr.plugin.core.api.synchronization.Clock;

public class TimedFileLogger extends FileLogger {
  private final Clock clock;

  public TimedFileLogger(String filepath) throws FileNotFoundException {
    this(filepath, new Clock(), true, false);
  }

  public TimedFileLogger(StringWriter writer) {
    super(writer, false);
    clock = new Clock();
  }

  public TimedFileLogger(String filepath, Clock clock) throws FileNotFoundException {
    this(filepath, clock, true, false);
  }

  public TimedFileLogger(String filepath, Clock clock, boolean timeStamp, boolean temporaryFile)
      throws FileNotFoundException {
    super(filepath, timeStamp, temporaryFile);
    this.clock = clock;
  }

  public void update() {
    clock.tick();
    super.update(clock.time());
  }
}
