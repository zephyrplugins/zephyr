package zephyr.plugin.core.api.logfiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

public abstract class LogFile implements Labeled {
  public final Clock clock;
  protected BufferedReader reader;
  final public String filepath;
  @Monitor(skipLabel = true)
  private final double[] current;
  private final String[] labels;

  public LogFile(String filepath) throws IOException {
    this.filepath = filepath;
    reader = getReader(filepath);
    labels = readLabels();
    current = new double[labels.length];
    clock = new Clock();
  }

  @LabelProvider(ids = { "current" })
  protected String labelOf(int i) {
    return labels[i];
  }

  public boolean eof() {
    if (reader == null)
      return true;
    if (clock.isTerminated())
      return true;
    boolean isReady = false;
    synchronized (reader) {
      try {
        isReady = reader.ready();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return !isReady;
  }

  abstract protected BufferedReader getReader(String filepath) throws IOException;

  protected String[] readLabels() {
    String line = readLine();
    while (line.startsWith("#") || line.startsWith(" "))
      line = line.substring(1);
    String[] labels = line.split(" ");
    makeLabelsUnique(labels);
    return labels;
  }

  private void makeLabelsUnique(String[] labels) {
    Map<String, Integer> nbInstances = new LinkedHashMap<String, Integer>();
    for (int i = 0; i < labels.length; i++) {
      Integer knownInstance = nbInstances.get(labels[i]);
      if (knownInstance != null)
        labels[i] = labels[i] + " (" + String.valueOf(knownInstance + 1) + ")";
      else
        knownInstance = 0;
      knownInstance++;
      nbInstances.put(labels[i], knownInstance);
    }
  }

  private String readLine() {
    if (reader == null)
      return null;
    String line = null;
    synchronized (reader) {
      try {
        line = reader.readLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (line.startsWith("#"))
      return null;
    if (line.isEmpty())
      return null;
    return line;
  }

  public void step() {
    String line = null;
    while (!eof() && line == null)
      line = readLine();
    if (line != null)
      lineToData(line);
    clock.tick();
  }

  private void lineToData(String line) {
    assert line != null;
    String[] sarray = line.split(" ");
    for (int i = 0; i < Math.min(sarray.length, current.length); i++)
      try {
        current[i] = Double.valueOf(sarray[i]);
      } catch (Exception e) {
      }
  }

  public static LogFile load(String filename) {
    try {
      if (filename.endsWith(".gz"))
        return new ZippedLogFile(filename);
      if (filename.endsWith(".bz2"))
        return new BZippedLogFile(filename);
      return new TextLogFile(filename);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void close() {
    if (reader == null)
      return;
    synchronized (reader) {
      try {
        reader.close();
        reader = null;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public String label() {
    return new File(filepath).getName();
  }

  public Clock clock() {
    return clock;
  }

  public double[] currentLine() {
    return current;
  }

  public String[] labels() {
    return labels;
  }
}
