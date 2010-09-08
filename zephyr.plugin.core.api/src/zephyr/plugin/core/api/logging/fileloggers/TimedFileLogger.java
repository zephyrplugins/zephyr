package zephyr.plugin.core.api.logging.fileloggers;

import java.io.FileNotFoundException;
import java.io.StringWriter;

import zephyr.plugin.core.api.synchronization.Clock;

public class TimedFileLogger extends FileLogger {
  private final Clock clock;
  private final boolean autoTick;

  public TimedFileLogger(String filepath) throws FileNotFoundException {
    this(filepath, new Clock(), true, false, false);
  }

  public TimedFileLogger(StringWriter writer) {
    super(writer, false);
    clock = new Clock();
    autoTick = true;
  }

  public TimedFileLogger(String filepath, Clock clock) throws FileNotFoundException {
    this(filepath, clock, false, true, false);
  }

  public TimedFileLogger(String filepath, Clock clock, boolean autoTick, boolean timeStamp, boolean temporaryFile)
      throws FileNotFoundException {
    super(filepath, timeStamp, temporaryFile);
    this.clock = clock;
    this.autoTick = autoTick;
  }

  public void update() {
    if (autoTick)
      clock.tick();
    long tickTime = autoTick ? clock.time() : clock.lastUpdateTime();
    super.update(tickTime);
  }
}
