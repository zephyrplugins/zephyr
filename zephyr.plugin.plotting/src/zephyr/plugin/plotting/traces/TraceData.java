package zephyr.plugin.plotting.traces;

import java.util.LinkedHashSet;
import java.util.Set;

import rlpark.plugin.utils.histories.AveragedHistory;
import rlpark.plugin.utils.histories.History;
import zephyr.plugin.plotting.traces.TracesSelection.TraceSelector;

public class TraceData {
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
    for (int timeScale = 0; timeScale < histories.length; timeScale++) {
      if (timeScale <= baseHistoryIndex)
        histories[timeScale] = shortTimeHistory;
      else
        histories[timeScale] = new AveragedHistory((int) Math.pow(10, timeScale) / HistoryLength,
                                                   HistoryLength);
    }
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

  protected void update(int stepTime) {
    double value = trace.logged.loggedValue(stepTime);
    for (int i = baseHistoryIndex; i < histories.length; i++)
      histories[i].append(value);
  }

  public History history(double historyTimeLength) {
    return histories[toHistoryIndex(historyTimeLength)];
  }

  public int onTimeAveraged(int currentHistoryLength) {
    History history = history(currentHistoryLength);
    if (history instanceof AveragedHistory)
      return ((AveragedHistory) history).period;
    return 1;
  }

  public int history(double historyTimeLength, float[] values) {
    final History history = histories[toHistoryIndex(historyTimeLength)];
    history.toArray(values);
    return history.shift();
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
