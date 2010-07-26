package zephyr.plugin.critterview;

import java.io.IOException;
import java.util.List;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.Utils;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.filehandling.IFileHandler;
import critterbot.crtrlog.LogFile;

public class FileHandler implements IFileHandler {

  @Override
  public List<String> extensions() {
    return Utils.asList("bz2", "gz", "crtrlog");
  }

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    LogFile logfile = LogFile.load(filepath);
    Logger logger = ZephyrPlotting.createLogger(Labels.label(logfile), logfile.clock);
    logger.add(logfile);
    while (logfile.hasNextStep())
      logfile.step();
    if (!logfile.clock().isKilled())
      logfile.close();
  }
}
