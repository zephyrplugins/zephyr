package zephyr.plugin.plotting.plot2d.drawer2d;

import org.eclipse.swt.graphics.GC;

public class Drawers {
  static public final Drawer2D Points = new Drawer2D() {
    @Override
    public void draw(GC gc, float[] xdata, float[] ydata, int[] gx, int[] gy) {
      for (int i = 0; i < gy.length; i++)
        gc.drawPoint(gx[i], gy[i]);
    }
  };
  static public final Drawer2D Lines = new Drawer2D() {
    @Override
    public void draw(GC gc, float[] xdata, float[] ydata, int[] gx, int[] gy) {
      for (int i = 1; i < gx.length; i++)
        gc.drawLine(gx[i - 1], gy[i - 1], gx[i], gy[i]);
    }
  };
  static public final Drawer2D Dots = new Drawer2D() {
    final static private int Radius = 1;

    @Override
    public void draw(GC gc, float[] xdata, float[] ydata, int[] gx, int[] gy) {
      for (int i = 1; i < gx.length; i++)
        gc.drawOval(gx[i] - Radius, gy[i] - Radius, Radius * 2, Radius * 2);
    }
  };
}
