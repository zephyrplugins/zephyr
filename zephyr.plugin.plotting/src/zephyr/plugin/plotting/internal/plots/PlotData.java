package zephyr.plugin.plotting.internal.plots;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.plotting.internal.traces.TraceData;
import zephyr.plugin.plotting.internal.traces.TraceData.DataTimeInfo;

public class PlotData {
  public class HistoryCached {
    final float[] values;
    final public DataTimeInfo timeInfo = new DataTimeInfo();

    HistoryCached(int size) {
      values = new float[size];
    }
  }

  public class RequestResult {
    public final HistoryCached history;
    public final int x;
    public final double y;
    public final String label;
    public final List<String> secondaryLabels;
    public final int synchronizationTime;
    public final int dataAge;
    public final Chrono resultTime = new Chrono();
    private final TraceData traceData;

    protected RequestResult(int traceIndex, int x, List<Integer> secondaryResults) {
      history = histories.get(traceIndex);
      this.x = x;
      traceData = selection.get(traceIndex);
      label = traceData.trace.label;
      y = history.values[x];
      dataAge = traceData.dataAge(history.timeInfo, history.values.length - x - 1);
      synchronizationTime = history.timeInfo.synchronizationTime;
      secondaryLabels = new ArrayList<String>();
      for (Integer index : secondaryResults)
        secondaryLabels.add(selection.get(index).trace.label);
    }
  }

  public static final int MinimumTimeLength = 10;
  public static final int MaximumTimeLength = (int) TraceData.MaxTimeLength;

  private final Listener<List<TraceData>> selectionListener = new Listener<List<TraceData>>() {
    @Override
    public void listen(List<TraceData> selected) {
      selectionChanged();
    }
  };
  private final Listener<Integer> historyListener = new Listener<Integer>() {
    @Override
    public void listen(Integer history) {
      selectionChanged();
    }
  };
  final PlotSelection selection;
  final List<HistoryCached> histories = new ArrayList<HistoryCached>();
  int currentHistoryLength = 10;
  private int historyArrayLength = 0;

  public PlotData(PlotSelection selection) {
    this.selection = selection;
    selection.onSelectedTracesChanged.connect(selectionListener);
    selection.onHistoryChanged.connect(historyListener);
  }

  protected void selectionChanged() {
    histories.clear();
  }

  synchronized public void synchronize() {
    if (selection.isEmpty())
      return;
    if (histories.isEmpty())
      prepareHistories();
    for (int i = 0; i < selection.size(); i++) {
      TraceData traceData = selection.get(i);
      HistoryCached history = histories.get(i);
      traceData.history(currentHistoryLength, history.values, history.timeInfo);
    }
  }

  private void prepareHistories() {
    historyArrayLength = TraceData.computeArrayLength(currentHistoryLength);
    for (int i = 0; i < selection.size(); i++)
      histories.add(new HistoryCached(historyArrayLength));
  }

  synchronized public List<HistoryCached> getHistories() {
    if (histories.isEmpty())
      synchronize();
    return new ArrayList<HistoryCached>(histories);
  }

  synchronized public RequestResult search(Point2D.Double dataPoint, double yRes) {
    if (histories.isEmpty())
      return null;
    int closestTraceIndex = -1;
    double closestDistance = 0;
    double closestValue = 0;
    int x = (int) Math.round(dataPoint.x);
    x = Math.max(0, x);
    x = Math.min(histories.get(0).values.length - 1, x);
    for (int traceIndex = 0; traceIndex < selection.size(); traceIndex++) {
      double value = histories.get(traceIndex).values[x];
      double distance = Math.abs(dataPoint.y - value);
      if (closestTraceIndex < 0 || distance < closestDistance) {
        closestTraceIndex = traceIndex;
        closestDistance = distance;
        closestValue = value;
      }
    }
    List<Integer> secondaryResults = new ArrayList<Integer>();
    for (int traceIndex = 0; traceIndex < selection.size(); traceIndex++) {
      if (traceIndex == closestTraceIndex)
        continue;
      double value = histories.get(traceIndex).values[x];
      if (Math.abs(closestValue - value) < yRes)
        secondaryResults.add(traceIndex);
    }
    return new RequestResult(closestTraceIndex, x, secondaryResults);
  }

  synchronized public boolean setHistoryLengthIFN(int historyLength) {
    if (historyLength == currentHistoryLength ||
        historyLength >= TraceData.MaxTimeLength ||
        historyLength <= 0)
      return false;
    currentHistoryLength = historyLength;
    selection.onHistoryChanged.fire(currentHistoryLength);
    return true;
  }

  public PlotSelection selection() {
    return selection;
  }

  public int historyLength() {
    return currentHistoryLength;
  }

  public boolean isEmpty() {
    return selection.isEmpty();
  }

  public int size() {
    return selection.size();
  }
}
