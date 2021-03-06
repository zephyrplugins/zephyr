package zephyr.plugin.core.api.internal.monitoring.fileloggers;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitoredDataTraverser;
import zephyr.plugin.core.api.internal.monitoring.helpers.Parser;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class FileLogger extends AbstractFileLogger implements DataMonitor {
  private final List<String> labels = new ArrayList<String>();
  private final List<Monitored> loggeds = new ArrayList<Monitored>();
  private final List<Boolean> atLeastOneInfinite = new ArrayList<Boolean>();
  private final List<Boolean> atLeastOneNaN = new ArrayList<Boolean>();
  private final List<FileLogger> newclocks = new ArrayList<FileLogger>();
  private final boolean timeStamps;
  private boolean legendWrote = false;

  public FileLogger(String filepath) throws IOException {
    this(filepath, false, false);
  }

  public FileLogger(Writer writer) {
    this(writer, false);
  }

  public FileLogger(Writer writer, boolean timeStamps) {
    super(writer);
    this.timeStamps = timeStamps;
    init();
    legendWrote = true;
  }

  public FileLogger(String filepath, boolean timeStamps, boolean temporaryFile) throws IOException {
    super(filepath, temporaryFile);
    this.timeStamps = timeStamps;
    init();
  }

  private void init() {
    if (timeStamps)
      labels.add("LocalTime");
  }

  @Override
  public void add(String label, Monitored monitored) {
    labels.add(label);
    loggeds.add(monitored);
    atLeastOneInfinite.add(false);
    atLeastOneNaN.add(false);
  }

  public void add(Object toAdd) {
    add(toAdd, MonitoredDataTraverser.MonitorEverythingLevel);
  }

  public void add(Object toAdd, int levelRequired) {
    Parser.parse(this, toAdd, levelRequired);
  }

  public void update(long stepTime) {
    if (!legendWrote) {
      printLegend();
      legendWrote = true;
    }
    printValues(stepTime);
  }

  public void printLegend() {
    file.println(getLegend());
  }

  private void printValues(long stepTime) {
    String valuesLine = valuesToLine(stepTime);
    if (!valuesLine.isEmpty())
      file.println(valuesLine);
    file.flush();
  }

  private String valuesToLine(long stepTime) {
    StringBuilder line = new StringBuilder();
    if (timeStamps) {
      line.append(stepTime);
      line.append(" ");
    }
    for (int i = 0; i < loggeds.size(); i++) {
      double value = loggeds.get(i).monitoredValue();
      line.append(value);
      if (Double.isInfinite(value) && !atLeastOneInfinite.get(i)) {
        System.err.println(String.format("Warning: %s is infinite", labels.get(i)));
        atLeastOneInfinite.set(i, true);
      }
      if (Double.isNaN(value) && !atLeastOneNaN.get(i)) {
        System.err.println(String.format("Warning: %s is NaN", labels.get(i)));
        atLeastOneNaN.set(i, true);
      }
      if (i < loggeds.size() - 1)
        line.append(" ");
    }
    return line.toString();
  }

  private String getLegend() {
    StringBuilder legend = new StringBuilder();
    for (String label : labels)
      legend.append(label + " ");
    if (legend.length() == 0)
      return "";
    return legend.toString().substring(0, legend.length() - 1);
  }

  @Override
  public void close() {
    super.close();
    for (FileLogger logger : newclocks)
      logger.close();
  }

  public String[] getLabels() {
    String[] result = new String[labels.size()];
    labels.toArray(result);
    return result;
  }
}
