package zephyr.plugin.plotting.plot2d;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.plotting.mousesearch.MouseSearchable;
import zephyr.plugin.plotting.mousesearch.RequestResult;
import zephyr.plugin.plotting.plot2d.drawer2d.Drawer2D;
import zephyr.plugin.plotting.plot2d.drawer2d.Drawers;

public class Plot2D implements MouseSearchable {
  public final Colors colors = new Colors();
  private final Axes axes = new Axes();
  private final Set<Data2D> datas = new HashSet<Data2D>();

  synchronized public void clear(GC gc) {
    datas.clear();
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
  }

  synchronized public void draw(GC gc, Data2D data) {
    draw(gc, Drawers.Lines, data);
  }

  synchronized public void draw(GC gc, Drawer2D drawer, Data2D data) {
    datas.add(data);
    float[] xdata = data.xdata;
    float[] ydata = data.ydata;
    assert xdata.length > 0 && xdata.length == ydata.length;
    for (float x : xdata)
      axes.x.update(x);
    for (float y : ydata)
      axes.y.update(y);
    axes.updateScaling(gc.getClipping());
    int[] gx = new int[xdata.length];
    int[] gy = new int[xdata.length];
    for (int i = 0; i < gy.length; i++) {
      gx[i] = axes.toGX(xdata[i]);
      gy[i] = axes.toGY(ydata[i]);
    }
    drawer.draw(gc, xdata, ydata, gx, gy);
  }

  public void resetAxes() {
    axes.x.reset();
    axes.y.reset();
  }

  @Override
  synchronized public RequestResult search(Point mousePosition) {
    Point2D.Double dataPoint = axes.toD(mousePosition);
    Data2D bestData = null;
    int xIndex = -1;
    double bestDistance = Double.MAX_VALUE;
    for (Data2D data : datas)
      for (int i = 0; i < data.nbPoints; i++) {
        double distance = dataPoint.distance(data.xdata[i], data.ydata[i]);
        if (distance < bestDistance) {
          bestDistance = distance;
          xIndex = i;
          bestData = data;
        }
      }
    if (bestData == null)
      return null;
    return new Plot2DRequestResult(axes, bestData, xIndex);
  }

  @Override
  synchronized public boolean emptySearch() {
    return datas.isEmpty();
  }

  public Axes axes() {
    return axes;
  }
}
