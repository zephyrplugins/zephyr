package zephyr.plugin.filehandling.internal;

import java.io.IOException;
import java.util.List;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.internal.logfiles.LogFile;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.utils.Misc;
import zephyr.plugin.filehandling.IFileHandler;

public class DefaultHandler implements IFileHandler {

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    handle(filepath);
  }

  @Override
  public List<String> extensions() {
    return Misc.asList(".gz", ".bz2");
  }

  public static void handle(String filepath) {
    LogFile logfile = LogFile.load(filepath);
    Clock clock = new Clock(filepath);
    Zephyr.advertise(clock, logfile);
    while (clock.tick() && !logfile.eof())
      logfile.step();
    logfile.close();
  }
}
