package zephyr.plugin.core.api.logging.fileloggers;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.logging.LabelBuilder;
import zephyr.plugin.core.api.logging.abstracts.Logged;
import zephyr.plugin.core.api.logging.abstracts.LoggedContainer;
import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.logging.helpers.Parser;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;

public class FileLogger extends AbstractFileLogger implements Logger {
  private final List<String> labels = new ArrayList<String>();
  private final List<Logged> loggeds = new ArrayList<Logged>();
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

  @Override
  public void add(String label, Logged logged) {
    String loggedLabel = labelBuilder.buildLabel(label);
    labels.add(loggedLabel);
    loggeds.add(logged);
    atLeastOneInfinite.add(false);
    atLeastOneNaN.add(false);
  }

  @Override
  public void add(Object toAdd) {
    if (toAdd instanceof Logged)
      add(Labels.label(toAdd), (Logged) toAdd);
    if (toAdd instanceof LoggedContainer)
      ((LoggedContainer) toAdd).setLogger(this);
    Parser.findAnnotations(this, toAdd);
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

  @Override
  public Logger newClock(String label, final Clock clock) {
    char extensionSeparator = '.';
    String extension = filepath.substring(filepath.lastIndexOf(extensionSeparator));
    String newFilePath = filepath.substring(0, filepath.lastIndexOf(extensionSeparator)) +
        extensionSeparator + label + extension;
    final FileLogger logger;
    try {
      logger = new FileLogger(newFilePath, timeStamps, temporaryFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
    clock.onTick.connect(new Listener<Clock>() {
      @Override
      public void listen(Clock eventInfo) {
        logger.update(clock.time());
      }
    });
    newclocks.add(logger);
    return logger;
  }

  public String[] getLabels() {
    String[] result = new String[labels.size()];
    labels.toArray(result);
    return result;
  }
}
