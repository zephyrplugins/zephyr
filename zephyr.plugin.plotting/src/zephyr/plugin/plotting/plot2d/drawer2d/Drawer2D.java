package zephyr.plugin.plotting.plot2d.drawer2d;

import org.eclipse.swt.graphics.GC;

public interface Drawer2D {
  void draw(GC gc, float[] xdata, float[] ydata, int[] gx, int[] gy);
}