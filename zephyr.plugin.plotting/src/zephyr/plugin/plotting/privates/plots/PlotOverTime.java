package zephyr.plugin.plotting.privates.plots;

import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.internal.canvas.Painter;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.plotting.internal.axes.Axes;
import zephyr.plugin.plotting.privates.plots.PlotData.HistoryCached;
import zephyr.plugin.plotting.privates.traces.TraceData;

public class PlotOverTime implements Painter {
  static private final RGB[] colorsOrder = { Colors.COLOR_BLUE, Colors.COLOR_DARK_RED, Colors.COLOR_DARK_GREEN,
      Colors.COLOR_DARK_BLUE, Colors.COLOR_DARK_MAGENTA, Colors.COLOR_BLACK, Colors.COLOR_GREEN, Colors.COLOR_MAGENTA,
      Colors.COLOR_RED, Colors.COLOR_DARK_CYAN, Colors.COLOR_DARK_YELLOW, Colors.COLOR_CYAN };

  private enum ResetMode {
    NoReset,
    BothAxes,
    AxeXOnly
  };

  private final Listener<List<TraceData>> selectionListener = new Listener<List<TraceData>>() {
    @Override
    public void listen(List<TraceData> selected) {
      resetAxes(false);
    }
  };
  private final Listener<Integer> historyListener = new Listener<Integer>() {
    @Override
    public void listen(Integer history) {
      resetAxes(false);
    }
  };
  private final Axes axes = new Axes();
  private final PlotData plotdata;
  private final Colors colors = new Colors();
  private ResetMode axesNeedReset = ResetMode.NoReset;
  private final HashSet<Integer> hashedLines = new HashSet<Integer>();
  private int lineWidth = 1;
  private boolean antiAliasing = false;

  public PlotOverTime(PlotData plotdata) {
    this.plotdata = plotdata;
    plotdata.selection().onSelectedTracesChanged.connect(selectionListener);
    plotdata.selection().onHistoryChanged.connect(historyListener);
  }

  public void preparePainting() {
    switch (axesNeedReset) {
    case NoReset:
      return;
    case AxeXOnly:
      axes.x.reset();
      break;
    case BothAxes:
      axes.x.reset();
      axes.y.reset();
      break;
    }
  }

  @Override
  public void paint(PainterMonitor painterListener, Image image, GC gc) {
    updatePreferences();
    gc.setAntialias(antiAliasing ? SWT.ON : SWT.OFF);
    preparePainting();
    List<HistoryCached> histories = plotdata.getHistories();
    updateAxes(histories);
    prepareDrawingZone(gc);
    if (histories.isEmpty())
      return;
    drawTraces(painterListener, gc, histories);
  }

  private void updatePreferences() {
    antiAliasing = ZephyrPlotting.preferredAntiAliasing();
    lineWidth = ZephyrPlotting.preferredLineWidth();
  }

  private void updateAxes(List<HistoryCached> histories) {
    if (histories.isEmpty()) {
      axes.x.update(0);
      axes.y.update(0);
      return;
    }
    final int timeLength = histories.get(0).values.length - 1;
    axes.x.update(0);
    axes.x.update(timeLength);
    for (HistoryCached history : histories)
      for (double value : history.values)
        axes.y.update(value);
    axesNeedReset = ResetMode.NoReset;
  }

  private void prepareDrawingZone(GC gc) {
    axes.updateScaling(gc.getClipping());
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
    gc.setLineWidth(lineWidth);
  }

  private void drawTraces(PainterMonitor painterMonitor, GC gc, List<HistoryCached> histories) {
    int timeLength = histories.get(0).values.length;
    for (int timeIndex = 1; timeIndex < timeLength; timeIndex++) {
      int colorIndex = 0;
      final int t0 = timeIndex - 1, t1 = timeIndex;
      final int x0 = axes.toGX(t0), x1 = axes.toGX(t1);
      final int dy = gc.getClipping().height;
      hashedLines.clear();
      for (HistoryCached history : histories) {
        double v0 = history.values[t0], v1 = history.values[t1];
        int y0 = axes.toGY(v0), y1 = axes.toGY(v1);
        axes.y.update(v1);
        boolean contained = hashedLines.add(y0 * dy + y1);
        if (contained) {
          gc.setForeground(colors.color(gc, colorsOrder[colorIndex % colorsOrder.length]));
          gc.drawLine(x0, y0, x1, y1);
        }
        colorIndex += 1;
      }
      if (painterMonitor.isCanceled())
        return;
      painterMonitor.painterStep();
    }
  }

  public void resetAxes(boolean resetYAxe) {
    axesNeedReset = resetYAxe ? ResetMode.BothAxes : ResetMode.AxeXOnly;
  }

  public Axes getAxes() {
    return axes;
  }
}
