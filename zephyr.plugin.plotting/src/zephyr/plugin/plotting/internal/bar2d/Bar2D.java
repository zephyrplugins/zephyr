package zephyr.plugin.plotting.internal.bar2d;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.plotting.internal.axes.Axes;

public class Bar2D {
  public final Colors colors = new Colors();
  private final Axes axes = new Axes();
  private final BarData2D dataBuffer = new BarData2D(axes);
  private BarColorMap colorMap = defaultColorMap();

  public void clear(GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
  }

  static public BarColorMap defaultColorMap() {
    return new BarColorMap() {
      @Override
      public RGB toColor(int x, double value) {
        return value > 0 ? Colors.COLOR_RED : Colors.COLOR_BLUE;
      }
    };
  }

  public void draw(GC gc, double[] toplot) {
    if (toplot == null) {
      dataBuffer.reset();
      return;
    }
    dataBuffer.updateData(toplot);
    Rectangle clipping = gc.getClipping();
    updateAxis(toplot, clipping);
    int barWidth = Math.max(1, clipping.width / toplot.length);
    int origin = axes.toGY(0);
    gc.setForeground(colors.color(gc, Colors.COLOR_BLACK));
    int lineWidth = ZephyrPlotting.preferredLineWidth();
    gc.setLineWidth(lineWidth);
    boolean drawContour = barWidth > lineWidth * 2;
    for (int i = 0; i < toplot.length; i++) {
      double yValue = toplot[i];
      int barHeight = axes.y.toGLength((float) -yValue);
      if (barHeight == 0)
        continue;
      int gx = axes.toGX(i);
      gc.setBackground(colors.color(gc, colorMap.toColor(i, yValue)));
      gc.fillRectangle(gx, origin, barWidth, barHeight);
      if (drawContour)
        gc.drawRectangle(gx, origin, barWidth, barHeight);
    }
  }

  private void updateAxis(double[] toplot, Rectangle clipping) {
    axes.x.update(0);
    axes.x.update(toplot.length);
    for (double y : toplot)
      axes.y.update(y);
    axes.updateScaling(clipping);
  }

  public void resetAxes() {
    axes.x.reset();
    axes.y.reset();
  }

  public Axes axes() {
    return axes;
  }

  public void dispose() {
    colors.dispose();
  }

  public BarData2D dataBuffer() {
    return dataBuffer;
  }

  public void clearData() {
    dataBuffer.reset();
  }

  public void setColorMap(BarColorMap colorMap) {
    this.colorMap = colorMap;
  }
}
