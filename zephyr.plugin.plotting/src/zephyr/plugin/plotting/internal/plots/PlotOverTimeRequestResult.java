package zephyr.plugin.plotting.internal.plots;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;

import zephyr.plugin.core.api.monitoring.LabelBuilder;
import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.plotting.internal.plots.PlotData.HistoryCached;
import zephyr.plugin.plotting.internal.traces.TraceData;
import zephyr.plugin.plotting.mousesearch.RequestResult;
import zephyr.plugin.plotting.plot2d.Axes;

public class PlotOverTimeRequestResult implements RequestResult {
  public final HistoryCached history;
  public final int x;
  public final double y;
  public final String label;
  public final List<String> secondaryLabels;
  public final int synchronizationTime;
  public final int dataAge;
  public final Chrono resultTime = new Chrono();
  private final TraceData traceData;
  private final Axes axes;

  protected PlotOverTimeRequestResult(Axes axes, PlotSelection selection, List<HistoryCached> histories,
      int traceIndex, int x, List<Integer> secondaryResults) {
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
    this.axes = axes;
  }

  @Override
  public Point computeMousePosition() {
    int timeOffset = history.timeInfo.synchronizationTime - synchronizationTime;
    assert timeOffset >= 0;
    int currentPositionX = x - timeOffset / history.timeInfo.period;
    return axes.toG(currentPositionX, y);
  }

  @Override
  public String fieldLabel() {
    String fieldLabel = label;
    int lastPrefix = label.lastIndexOf(LabelBuilder.DefaultSeparator);
    if (lastPrefix > 0 && lastPrefix < label.length() - 1)
      fieldLabel = label.substring(lastPrefix + 1);
    return String.format("%s: %f [T: %d]", fieldLabel, y, dataAge);
  }

  @Override
  public String tooltipLabel() {
    StringBuilder tooltipLabel = new StringBuilder(String.format("T: %d Val: %f\n%s", dataAge, y, label));
    for (String secondaryLabel : secondaryLabels) {
      tooltipLabel.append("\n");
      tooltipLabel.append(secondaryLabel);
    }
    return tooltipLabel.toString();
  }
}