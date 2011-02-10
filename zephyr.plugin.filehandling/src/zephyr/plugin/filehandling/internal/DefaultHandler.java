package zephyr.plugin.filehandling.internal;

import java.io.IOException;
import java.util.List;

import zephyr.plugin.core.Utils;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.logfiles.LogFile;
import zephyr.plugin.filehandling.IFileHandler;

public class DefaultHandler implements IFileHandler {

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    handle(filepath);
  }

  @Override
  public List<String> extensions() {
    return Utils.asList(".gz", ".bz2");
  }

  public static void handle(String filepath) {
    LogFile logfile = LogFile.load(filepath);
    Zephyr.advertise(logfile);
    while (!logfile.eof())
      logfile.step();
    logfile.close();
  }
}
