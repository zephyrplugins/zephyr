package zephyr.plugin.plotting.internal.plots;

import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.canvas.Painter;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.plotting.internal.plots.PlotData.HistoryCached;
import zephyr.plugin.plotting.internal.traces.TraceData;
import zephyr.plugin.plotting.plot2d.Axes;

public class PlotOverTime implements Painter {
  static public RGB[] colorsOrder = { Colors.COLOR_BLUE, Colors.COLOR_DARK_RED, Colors.COLOR_DARK_GREEN,
      Colors.COLOR_DARK_BLUE, Colors.COLOR_DARK_MAGENTA, Colors.COLOR_BLACK, Colors.COLOR_GREEN, Colors.COLOR_MAGENTA,
      Colors.COLOR_RED, Colors.COLOR_DARK_CYAN, Colors.COLOR_DARK_YELLOW, Colors.COLOR_YELLOW, Colors.COLOR_CYAN };

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
  private Image lastImage = null;
  private int timeIndex = -1;
  private final HashSet<Integer> hashedLines = new HashSet<Integer>();

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
      resetTimeIndex();
      axes.x.reset();
      break;
    case BothAxes:
      resetTimeIndex();
      axes.x.reset();
      axes.y.reset();
      break;
    }
  }

  private void resetTimeIndex() {
    timeIndex = -1;
  }

  @Override
  public boolean paint(Image image, GC gc) {
    if (lastImage != image) {
      resetTimeIndex();
      resetAxes(true);
      lastImage = image;
    }
    gc.setAntialias(SWT.OFF);
    preparePainting();
    List<HistoryCached> histories = plotdata.getHistories();
    if (axesNeedReset != ResetMode.NoReset)
      updateAxes(histories);
    if (histories.isEmpty())
      return true;
    if (timeIndex == -1)
      prepareDrawingZone(gc);
    drawTraces(gc, histories);
    return timeIndex == -1;
  }

  @Override
  public boolean newPaintingRequired() {
    return axes.y.scalingRequired();
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
    gc.setLineWidth(1);
  }

  private void drawTraces(GC gc, List<HistoryCached> histories) {
    hashedLines.clear();
    int colorIndex = 0;
    if (timeIndex == -1)
      timeIndex = histories.get(0).values.length - 1;
    final int t0 = timeIndex - 1, t1 = timeIndex;
    final int x0 = axes.toGX(t0), x1 = axes.toGX(t1);
    final int dy = gc.getClipping().height;
    for (HistoryCached history : histories) {
      double v0 = history.values[t0], v1 = history.values[t1];
      axes.y.update(v1);
      int y0 = axes.toGY(v0), y1 = axes.toGY(v1);
      boolean contained = hashedLines.add(y0 * dy + y1);
      if (contained) {
        gc.setForeground(colors.color(gc, colorsOrder[colorIndex % colorsOrder.length]));
        gc.drawLine(x0, y0, x1, y1);
      }
      colorIndex += 1;
    }
    timeIndex = timeIndex > 1 ? timeIndex - 1 : -1;
  }

  public void resetAxes(boolean resetYAxe) {
    axesNeedReset = resetYAxe ? ResetMode.BothAxes : ResetMode.AxeXOnly;
  }

  public Axes getAxes() {
    return axes;
  }
}