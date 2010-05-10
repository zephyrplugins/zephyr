package zephyr.plugin.plotting.plots;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.utils.events.Listener;
import zephyr.plugin.plotting.traces.TraceData;

public class PlotData {
  public class HistoryCached {
    final float[] values;
    public int time;

    HistoryCached(int size) {
      values = new float[size];
    }
  }

  public class RequestResult {
    public final HistoryCached history;
    public final int x;
    public final double y;
    public final int timeShift;
    public final String label;
    public final int time;

    protected RequestResult(int traceIndex, int x) {
      this.history = histories.get(traceIndex);
      this.x = x;
      TraceData traceData = selection.get(traceIndex);
      this.label = traceData.trace.label;
      y = history.values[x];
      timeShift = history.time;
      time = traceData.onTimeAveraged(currentHistoryLength) * (timeShift + x);
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
  int currentHistoryLength = MaximumTimeLength;
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
      history.time = traceData.history(currentHistoryLength, history.values);
    }
  }

  private void prepareHistories() {
    historyArrayLength = TraceData.computeArrayLength(currentHistoryLength);
    for (int i = 0; i < selection.size(); i++)
      histories.add(new HistoryCached(historyArrayLength));
  }

  public List<HistoryCached> getHistories() {
    if (histories.isEmpty())
      synchronize();
    return new ArrayList<HistoryCached>(histories);
  }

  synchronized public RequestResult search(Point2D.Double dataPoint) {
    if (histories.isEmpty())
      return null;
    int closestTraceIndex = -1;
    double closestDistance = 0;
    int bestX = 0;
    final int toleranceX = 5;
    int minX = Math.max(0, (int) dataPoint.x - toleranceX);
    int maxX = Math.min(historyArrayLength, (int) dataPoint.x + toleranceX);
    for (int currentDx = minX; currentDx < maxX; currentDx++) {
      double squaredDistanceX = (currentDx - dataPoint.x) * (currentDx - dataPoint.x);
      for (int traceIndex = 0; traceIndex < selection.size(); traceIndex++) {
        double value = histories.get(traceIndex).values[currentDx];
        double squaredDistanceY = (dataPoint.y - value) * (dataPoint.y - value);
        double squaredDistance = squaredDistanceX + squaredDistanceY;
        if ((closestTraceIndex < 0) || (squaredDistance < closestDistance)) {
          closestTraceIndex = traceIndex;
          closestDistance = squaredDistance;
          bestX = currentDx;
        }
      }
    }
    if (closestTraceIndex < 0)
      return null;
    return new RequestResult(closestTraceIndex, bestX);
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
