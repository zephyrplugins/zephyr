package zephyr.plugin.plotting.bar2d;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.plotting.axes.Axes;
import zephyr.plugin.plotting.data.Data2D;
import zephyr.plugin.plotting.mousesearch.MouseSearchable;
import zephyr.plugin.plotting.mousesearch.RequestResult;
import zephyr.plugin.plotting.plot2d.Plot2DRequestResult;

public class Bar2D implements MouseSearchable {
  public final Colors colors = new Colors();
  private final Axes axes = new Axes();
  private Data2D data = null;

  synchronized public void clear(GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
  }

  synchronized public void draw(GC gc, float[] toplot) {
    if (toplot == null) {
      data = null;
      return;
    }
    updateData(toplot);
    axes.x.update(0);
    axes.x.update(data.nbPoints);
    for (float y : data.ydata)
      axes.y.update(y);
    axes.updateScaling(gc.getClipping());
    float[] xdata = data.xdata;
    float[] ydata = data.ydata;
    int barWidth = Math.max(1, gc.getClipping().width / data.nbPoints);
    int origin = axes.toGY(0);
    gc.setForeground(colors.color(gc, Colors.COLOR_BLACK));
    for (int i = 0; i < data.nbPoints; i++) {
      float yValue = ydata[i];
      int barHeight = axes.y.toGLength(-yValue);
      if (barHeight == 0)
        continue;
      int gx = axes.toGX(xdata[i]);
      gc.setBackground(colors.color(gc, yValue > 0 ? Colors.COLOR_RED : Colors.COLOR_BLUE));
      gc.fillRectangle(gx, origin, barWidth, barHeight);
      if (barWidth > 5)
        gc.drawRectangle(gx, origin, barWidth, barHeight);
    }
  }

  private void updateData(float[] toplot) {
    if (data == null || toplot.length != data.nbPoints) {
      data = new Data2D(toplot.length);
      for (int i = 0; i < data.nbPoints; i++)
        data.xdata[i] = i;
    }
    System.arraycopy(toplot, 0, data.ydata, 0, data.nbPoints);
  }

  public void resetAxes() {
    axes.x.reset();
    axes.y.reset();
  }

  @Override
  synchronized public RequestResult search(Point mousePosition) {
    if (data == null)
      return null;
    return new Plot2DRequestResult(axes, data, (int) axes.toDX(mousePosition.x));
  }

  @Override
  synchronized public boolean emptySearch() {
    return data == null;
  }

  public Axes axes() {
    return axes;
  }
}
