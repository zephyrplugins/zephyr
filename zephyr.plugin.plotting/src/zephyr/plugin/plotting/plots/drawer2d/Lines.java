package zephyr.plugin.plotting.plots.drawer2d;

import org.eclipse.swt.graphics.GC;

public class Lines implements Drawer2D {

  @Override
  public void draw(GC gc, float[] dx, float[] dy, int[] gx, int[] gy) {
    gc.setLineWidth(1);
    for (int i = 1; i < gx.length; i++)
      gc.drawLine(gx[i - 1], gy[i - 1], gx[i], gy[i]);
  }
}
