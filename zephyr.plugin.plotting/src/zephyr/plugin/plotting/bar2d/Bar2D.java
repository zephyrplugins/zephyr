package zephyr.plugin.plotting.bar2d;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.plotting.axes.Axes;
import zephyr.plugin.plotting.mousesearch.MouseSearchable;

public class Bar2D {
  public final Colors colors = new Colors();
  private final Axes axes = new Axes();
  private final SearchableData dataBuffer = new SearchableData(axes);

  public void clear(GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
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
    int lineWidth = ZephyrPlotting.preferredLineSize();
    gc.setLineWidth(lineWidth);
    boolean drawContour = barWidth > lineWidth * 2;
    for (int i = 0; i < toplot.length; i++) {
      double yValue = toplot[i];
      int barHeight = axes.y.toGLength((float) -yValue);
      if (barHeight == 0)
        continue;
      int gx = axes.toGX(i);
      gc.setBackground(colors.color(gc, yValue > 0 ? Colors.COLOR_RED : Colors.COLOR_BLUE));
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

  public MouseSearchable dataBuffer() {
    return dataBuffer;
  }
}
