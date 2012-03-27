package zephyr.plugin.plotting.internal.plot2d;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.plotting.internal.axes.Axes;
import zephyr.plugin.plotting.internal.data.Data2D;
import zephyr.plugin.plotting.internal.mousesearch.MouseSearchable;
import zephyr.plugin.plotting.internal.plot2d.drawer2d.Drawer2D;
import zephyr.plugin.plotting.internal.plot2d.drawer2d.Drawers;

public class Plot2D {
  public final Colors colors = new Colors();
  private final Axes axes = new Axes();
  private final PlotData2D datas = new PlotData2D(axes);

  public void clear(GC gc) {
    datas.reset();
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
  }

  public void draw(GC gc, Data2D data) {
    draw(gc, Drawers.Lines, data);
  }

  public void draw(GC gc, Drawer2D drawer, Data2D data) {
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

  public Axes axes() {
    return axes;
  }

  public MouseSearchable dataBuffer() {
    return datas;
  }

  public void clearData() {
    datas.reset();
  }
}
