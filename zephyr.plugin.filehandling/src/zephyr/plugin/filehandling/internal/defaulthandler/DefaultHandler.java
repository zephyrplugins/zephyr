package zephyr.plugin.filehandling.internal.defaulthandler;

import java.io.IOException;
import java.util.List;

import zephyr.ZephyrCore;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.Utils;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
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
    ZephyrCore.advertise(logfile.clock(), logfile);
    DataMonitor logger = ZephyrPlotting.createLogger(Labels.label(logfile), logfile.clock);
    logger.add(logfile);
    while (!logfile.eof())
      logfile.step();
    logfile.close();
  }
}
