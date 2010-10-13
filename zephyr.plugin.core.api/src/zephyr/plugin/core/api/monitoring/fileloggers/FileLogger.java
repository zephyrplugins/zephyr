package zephyr.plugin.core.api.monitoring.fileloggers;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.LabelBuilder;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.helpers.Parser;

public class FileLogger extends AbstractFileLogger implements DataMonitor {
  private final List<String> labels = new ArrayList<String>();
  private final List<Monitored> loggeds = new ArrayList<Monitored>();
  private final List<Boolean> atLeastOneInfinite = new ArrayList<Boolean>();
  private final List<Boolean> atLeastOneNaN = new ArrayList<Boolean>();
  private final List<FileLogger> newclocks = new ArrayList<FileLogger>();
  private final LabelBuilder labelBuilder = new LabelBuilder("", "");
  private final boolean timeStamps;
  private boolean legendWrote = false;

  public FileLogger(String filepath) throws FileNotFoundException {
    this(filepath, false, false);
  }

  public FileLogger(StringWriter writer) {
    this(writer, false);
  }

  public FileLogger(StringWriter writer, boolean timeStamps) {
    super(writer);
    this.timeStamps = timeStamps;
    init();
  }

  public FileLogger(String filepath, boolean timeStamps, boolean temporaryFile) throws FileNotFoundException {
    super(filepath, temporaryFile);
    this.timeStamps = timeStamps;
    init();
  }

  private void init() {
    if (timeStamps)
      labels.add("LocalTime");
  }

  public void add(String label, Monitored logged) {
    add(label, logged, Parser.MonitorEverythingLevel);
  }

  @Override
  public void add(String label, Monitored logged, int level) {
    String loggedLabel = labelBuilder.buildLabel(label);
    labels.add(loggedLabel);
    loggeds.add(logged);
    atLeastOneInfinite.add(false);
    atLeastOneNaN.add(false);
  }

  @Override
  public void add(Object toAdd) {
    add(toAdd, Parser.MonitorEverythingLevel);
  }

  public void add(Object toAdd, int levelRequired) {
    if (toAdd instanceof Monitored)
      add(Labels.label(toAdd), (Monitored) toAdd);
    if (toAdd instanceof MonitorContainer)
      ((MonitorContainer) toAdd).setLogger(0, this);
    Parser.findAnnotations(this, toAdd, levelRequired);
  }

  public void update(long stepTime) {
    if (!legendWrote) {
      file.println(getLegend());
      legendWrote = true;
    }
    printValues(stepTime);
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
      double value = loggeds.get(i).loggedValue(stepTime);
      line.append(String.valueOf(value));
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
  public LabelBuilder labelBuilder() {
    return labelBuilder;
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
