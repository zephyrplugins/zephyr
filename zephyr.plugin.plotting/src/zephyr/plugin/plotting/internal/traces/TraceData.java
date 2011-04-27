package zephyr.plugin.plotting.internal.traces;

import java.util.LinkedHashSet;
import java.util.Set;

import zephyr.plugin.plotting.internal.histories.AveragedHistory;
import zephyr.plugin.plotting.internal.histories.History;
import zephyr.plugin.plotting.internal.traces.TracesSelection.TraceSelector;

public class TraceData {
  static public class DataTimeInfo {
    public int period;
    public long synchronizationTime;
    public int bufferedData;
  }

  static public final int HistoryLength = 1000;
  static public final double MaxTimeLength = 10e8;
  static private final int baseHistoryIndex = toHistoryIndex(HistoryLength - 1);

  final public Trace trace;
  final protected History[] histories;
  final private Set<TraceSelector> selectors = new LinkedHashSet<TraceSelector>();

  protected TraceData(Trace trace) {
    this.trace = trace;
    histories = createHistories();
  }

  private History[] createHistories() {
    History[] histories = new History[toHistoryIndex(MaxTimeLength)];
    History shortTimeHistory = new History(HistoryLength);
    for (int timeScale = 0; timeScale < histories.length; timeScale++)
      if (timeScale <= baseHistoryIndex)
        histories[timeScale] = shortTimeHistory;
      else
        histories[timeScale] = new AveragedHistory((int) Math.pow(10, timeScale) / HistoryLength,
                                                   HistoryLength);
    return histories;
  }

  protected void addSelector(TraceSelector selector) {
    assert !selectors.contains(selector);
    selectors.add(selector);
  }

  protected void removeSelector(TraceSelector selector) {
    assert selectors.contains(selector);
    selectors.remove(selector);
  }

  protected boolean isSelected() {
    return !selectors.isEmpty();
  }

  protected void update(long stepTime) {
    double value = trace.logged.monitoredValue(stepTime);
    if (Double.isNaN(value))
      value = 0;
    for (int i = baseHistoryIndex; i < histories.length; i++)
      histories[i].append(value);
  }

  public History history(double historyTimeLength) {
    return histories[toHistoryIndex(historyTimeLength)];
  }

  public long dataAge(DataTimeInfo timeInfo, int dataIndex) {
    return timeInfo.synchronizationTime - timeInfo.bufferedData - (long) dataIndex * timeInfo.period;
  }

  public void history(double historyTimeLength, float[] values, DataTimeInfo timeInfo) {
    final History history = histories[toHistoryIndex(historyTimeLength)];
    history.toArray(values);
    timeInfo.synchronizationTime = trace.clockTraces.clock.timeStep();
    if (history instanceof AveragedHistory) {
      AveragedHistory averageHistory = (AveragedHistory) history;
      timeInfo.bufferedData = averageHistory.nbBufferedData() - 1;
      timeInfo.period = averageHistory.period;
    } else {
      timeInfo.bufferedData = 0;
      timeInfo.period = 1;
    }
  }

  static private int toHistoryIndex(double historyTimeLength) {
    return (int) Math.floor(Math.log10(historyTimeLength)) + 1;
  }

  public static int computeArrayLength(int currentHistoryLength) {
    int historyIndex = Math.max(baseHistoryIndex, toHistoryIndex(currentHistoryLength));
    double timeDiff = Math.log10(currentHistoryLength) - historyIndex;
    double logArrayLength = Math.log10(HistoryLength) + timeDiff;
    int arrayLength = (int) Math.pow(10, logArrayLength);
    assert arrayLength > 0 && arrayLength <= HistoryLength;
    return arrayLength;
  }
}
