package zephyr.plugin.plotting.plots;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import rlpark.plugin.utils.events.Listener;
import zephyr.plugin.common.canvas.Painter;
import zephyr.plugin.common.utils.Colors;
import zephyr.plugin.plotting.plots.PlotData.HistoryCached;
import zephyr.plugin.plotting.traces.TraceData;

public class PlotOverTime implements Painter {
  static public RGB[] colorsOrder = { Colors.COLOR_BLUE, Colors.COLOR_DARK_RED, Colors.COLOR_DARK_GREEN,
      Colors.COLOR_CYAN, Colors.COLOR_DARK_BLUE, Colors.COLOR_DARK_CYAN,
      Colors.COLOR_DARK_MAGENTA, Colors.COLOR_DARK_YELLOW, Colors.COLOR_GREEN,
      Colors.COLOR_MAGENTA, Colors.COLOR_RED, Colors.COLOR_YELLOW };

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
      axes.xAxe().reset();
      break;
    case BothAxes:
      axes.xAxe().reset();
      axes.yAxe().reset();
      break;
    }
  }

  public boolean paint(Image image, GC gc) {
    preparePainting();
    gc.setAntialias(SWT.OFF);
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
    List<HistoryCached> histories = plotdata.getHistories();
    if (histories.isEmpty())
      return true;
    if (axesNeedReset != ResetMode.NoReset)
      updateAxes(histories);
    axes.updateScaling(gc.getClipping());
    drawDrawingZone(gc);
    drawTraces(gc, histories);
    return true;
  }

  private void updateAxes(List<HistoryCached> histories) {
    for (HistoryCached history : histories) {
      final float[] values = history.values;
      for (int t = 0; t < values.length; t++)
        axes.update(t, values[t]);
    }
    axesNeedReset = ResetMode.NoReset;
  }

  private void drawDrawingZone(GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(axes.drawingZone());
  }

  private void drawTraces(GC gc, List<HistoryCached> histories) {
    int colorIndex = 0;
    gc.setLineWidth(1);
    for (HistoryCached history : histories) {
      gc.setForeground(colors.color(gc, colorsOrder[colorIndex % colorsOrder.length]));
      drawTrace(gc, history.values);
      colorIndex += 1;
    }
  }

  private void drawTrace(GC gc, float[] data) {
    int px = 0, py = 0;
    for (int t = 0; t < data.length; t++) {
      double value = data[t];
      axes.update(t, value);
      int x = axes.toGX(t), y = axes.toGY(value);
      if (t > 0)
        gc.drawLine(px, py, x, y);
      else
        gc.drawPoint(x, y);
      px = x;
      py = y;
    }
  }

  public void resetAxes(boolean resetYAxe) {
    axesNeedReset = resetYAxe ? ResetMode.BothAxes : ResetMode.AxeXOnly;
  }

  public Axes getAxes() {
    return axes;
  }
}
