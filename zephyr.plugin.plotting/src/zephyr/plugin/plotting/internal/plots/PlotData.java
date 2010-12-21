package zephyr.plugin.plotting.internal.plots;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.plotting.internal.traces.TraceData;
import zephyr.plugin.plotting.internal.traces.TraceData.DataTimeInfo;
import zephyr.plugin.plotting.mousesearch.RequestResult;
import zephyr.plugin.plotting.plot2d.Axes;

public class PlotData {
  static public class HistoryCached {
    final float[] values;
    final public DataTimeInfo timeInfo = new DataTimeInfo();

    HistoryCached(int size) {
      values = new float[size];
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

  synchronized public boolean synchronize() {
    if (selection.isEmpty())
      return false;
    if (histories.isEmpty())
      prepareHistories();
    for (int i = 0; i < selection.size(); i++) {
      TraceData traceData = selection.get(i);
      HistoryCached history = histories.get(i);
      traceData.history(currentHistoryLength, history.values, history.timeInfo);
    }
    return true;
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

  synchronized public RequestResult search(Axes axes, Point mousePosition) {
    Point2D.Double dataPoint = axes.toD(mousePosition);
    double yRes = axes.scaleToDY(1);
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
    return new PlotOverTimeRequestResult(axes, selection, histories, closestTraceIndex, x, secondaryResults);
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
