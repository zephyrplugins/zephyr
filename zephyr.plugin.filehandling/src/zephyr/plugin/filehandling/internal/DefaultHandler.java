package zephyr.plugin.filehandling.internal;

import java.io.IOException;
import java.util.List;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.internal.logfiles.LogFile;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
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
    final LogFile logfile = LogFile.load(filepath);
    Clock clock = new Clock(filepath);
    Zephyr.advertise(clock, logfile);
    if (logfile.labels().length < 100)
      registerLabels(logfile, clock);
    while (clock.tick() && !logfile.eof())
      logfile.step();
    logfile.close();
    clock.terminate();
  }

  private static void registerLabels(final LogFile logfile, Clock clock) {
    Zephyr.advertise(clock, new MonitorContainer() {
      @Override
      public void addToMonitor(DataMonitor monitor) {
        String[] labels = logfile.labels();
        for (int i = 0; i < labels.length; i++) {
          final int index = i;
          monitor.add(labels[index], new Monitored() {
            @Override
            public double monitoredValue() {
              return logfile.currentLine()[index];
            }
          });
        }
      }
    }, "data");
  }
}
