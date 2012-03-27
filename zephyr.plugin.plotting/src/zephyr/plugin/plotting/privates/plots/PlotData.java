package zephyr.plugin.plotting.privates.plots;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.eclipse.swt.graphics.Point;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.plotting.internal.axes.Axes;
import zephyr.plugin.plotting.internal.mousesearch.RequestResult;
import zephyr.plugin.plotting.privates.traces.TraceData;
import zephyr.plugin.plotting.privates.traces.TraceData.DataTimeInfo;

public class PlotData {
  static public class HistoryCached {
    final float[] values;
    final public DataTimeInfo timeInfo = new DataTimeInfo();

    HistoryCached(int size) {
      values = new float[size];
    }
  }

  class SyncHistory {
    final List<HistoryCached> histories = new ArrayList<HistoryCached>();
    private final Semaphore semaphore = new Semaphore(1);

    List<HistoryCached> lockHistory(List<TraceData> selection) {
      lock();
      if (histories.size() != selection.size() || selectionChanged)
        prepareHistories(selection);
      return histories;
    }

    private void lock() {
      try {
        semaphore.acquire();
      } catch (InterruptedException e) {
      }
    }

    private void prepareHistories(List<TraceData> selection) {
      selectionChanged = false;
      historyArrayLength = TraceData.computeArrayLength(currentHistoryLength);
      histories.clear();
      for (int i = 0; i < selection.size(); i++)
        histories.add(new HistoryCached(historyArrayLength));
    }

    void unlock() {
      semaphore.release();
    }

    List<HistoryCached> getHistories(List<TraceData> selection) {
      ArrayList<PlotData.HistoryCached> result = new ArrayList<PlotData.HistoryCached>(lockHistory(selection));
      unlock();
      return result;
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
  final PlotSelection plotSelection;
  int currentHistoryLength = 80;
  int historyArrayLength = 0;
  boolean selectionChanged = false;
  private final SyncHistory syncHistory = new SyncHistory();

  public PlotData(PlotSelection selection) {
    this.plotSelection = selection;
    selection.onSelectedTracesChanged.connect(selectionListener);
    selection.onHistoryChanged.connect(historyListener);
  }

  protected void selectionChanged() {
    selectionChanged = true;
  }

  public boolean synchronize(Clock clock) {
    List<TraceData> selection = plotSelection.getSelection();
    if (selection.isEmpty())
      return false;
    List<HistoryCached> histories = syncHistory.lockHistory(selection);
    for (int i = 0; i < selection.size(); i++) {
      TraceData traceData = selection.get(i);
      if (traceData.trace.clock() != clock)
        continue;
      HistoryCached history = histories.get(i);
      traceData.history(clock, currentHistoryLength, history.values, history.timeInfo);
    }
    syncHistory.unlock();
    return true;
  }

  public List<HistoryCached> getHistories() {
    return syncHistory.getHistories(plotSelection.getSelection());
  }

  public RequestResult search(Axes axes, Point mousePosition) {
    Point2D.Double dataPoint = axes.toD(mousePosition);
    double yRes = axes.scaleToDY(1);
    List<TraceData> selection = plotSelection.getSelection();
    List<HistoryCached> histories = syncHistory.getHistories(selection);
    if (histories.isEmpty() || selection.size() != histories.size())
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

  public boolean setHistoryLengthIFN(int historyLength) {
    if (historyLength == currentHistoryLength || historyLength >= TraceData.MaxTimeLength || historyLength <= 0)
      return false;
    currentHistoryLength = historyLength;
    selectionChanged();
    plotSelection.onHistoryChanged.fire(currentHistoryLength);
    return true;
  }

  public PlotSelection selection() {
    return plotSelection;
  }

  public int historyLength() {
    return currentHistoryLength;
  }
}
