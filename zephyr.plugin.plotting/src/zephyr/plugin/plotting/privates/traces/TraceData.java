package zephyr.plugin.plotting.privates.traces;

import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ReferenceManager;
import zephyr.plugin.plotting.privates.histories.AveragedHistory;
import zephyr.plugin.plotting.privates.histories.History;

public class TraceData {
  static public class DataTimeInfo {
    public int period = 1;
    public long synchronizationTime;
    public int bufferedData;
  }

  static public final int HistoryLength = 1000;
  static public final double MaxTimeLength = 10e8;
  static private final int baseHistoryIndex = toHistoryIndex(HistoryLength - 1);
  final public Monitored monitored;
  final public Trace trace;
  final protected History[] histories;
  private int ref = 0;

  protected TraceData(Trace trace, Monitored monitored) {
    this.trace = trace;
    this.monitored = monitored;
    histories = createHistories();
  }

  private static History[] createHistories() {
    History[] histories = new History[toHistoryIndex(MaxTimeLength)];
    History shortTimeHistory = new History(HistoryLength);
    for (int timeScale = 0; timeScale < histories.length; timeScale++)
      if (timeScale <= baseHistoryIndex)
        histories[timeScale] = shortTimeHistory;
      else
        histories[timeScale] = new AveragedHistory((int) Math.pow(10, timeScale) / HistoryLength, HistoryLength);
    return histories;
  }

  protected void update(long stepTime) {
    float value = (float) monitored.monitoredValue();
    if (Float.isNaN(value))
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

  public void history(Clock clock, double historyTimeLength, float[] values, DataTimeInfo timeInfo) {
    final History history = histories[toHistoryIndex(historyTimeLength)];
    history.toArray(values);
    timeInfo.synchronizationTime = clock.timeStep();
    if (history instanceof AveragedHistory) {
      AveragedHistory averageHistory = (AveragedHistory) history;
      timeInfo.bufferedData = averageHistory.nbBufferedData() - 1;
      timeInfo.period = averageHistory.period > 0 ? averageHistory.period : 1;
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

  public void incRef() {
    ref++;
    ReferenceManager.manager().incRef(trace.codeNode);
  }

  public boolean decRef() {
    ref--;
    ReferenceManager.manager().decRef(trace.codeNode);
    return (ref == 0);
  }

  public int ref() {
    assert ref >= 0;
    return ref;
  }
}
