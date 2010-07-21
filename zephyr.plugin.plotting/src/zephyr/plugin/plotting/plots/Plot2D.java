package zephyr.plugin.plotting.plots;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

import zephyr.plugin.common.utils.Colors;
import zephyr.plugin.plotting.plots.drawer2d.Drawer2D;
import zephyr.plugin.plotting.plots.drawer2d.Lines;

public class Plot2D {
  private final Axes axes = new Axes();
  private final Colors colors = new Colors();
  private final Drawer2D lineDrawer = new Lines();

  public void clear(GC gc) {
    gc.setAntialias(SWT.OFF);
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
  }

  public void draw(GC gc, float[] xdata, float[] ydata) {
    draw(gc, lineDrawer, xdata, ydata);
  }

  public void draw(GC gc, Drawer2D drawer, float[] xdata, float[] ydata) {
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
}
