package zephyr.plugin.plotting.plots;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

import zephyr.plugin.common.utils.Colors;

public class Plot2D {

  private final Axes axes = new Axes();
  private final Colors colors = new Colors();

  public void draw(GC gc, double[] xdata, double[] ydata) {
    assert xdata.length > 0 && xdata.length == ydata.length;
    for (double x : xdata)
      axes.x.update(x);
    for (double y : ydata)
      axes.y.update(y);
    gc.setAntialias(SWT.OFF);
    axes.updateScaling(gc.getClipping());
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
    gc.setLineWidth(1);
    int x1 = axes.toGX(xdata[0]);
    int y1 = axes.toGY(0);
    for (int i = 1; i < xdata.length; i++) {
      int x2 = axes.toGX(xdata[i]);
      int y2 = axes.toGY(ydata[i]);
      gc.drawLine(x1, y1, x2, y2);
      x1 = x2;
      y1 = y2;
    }
  }
}
