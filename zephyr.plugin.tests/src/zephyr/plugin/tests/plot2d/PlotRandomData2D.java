package zephyr.plugin.tests.plot2d;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.core.canvas.AbstractCanvasView;
import zephyr.plugin.core.views.TimedView;
import zephyr.plugin.core.views.ViewProvider;
import zephyr.plugin.plotting.plot2d.Plot2D;


public class PlotRandomData2D extends AbstractCanvasView implements TimedView {
  public static class Provider implements ViewProvider {
    @Override
    public String viewID() {
      return "zephyr.plugin.tests.plot2d.plotrandomplot2d";
    }

    @Override
    public boolean canViewDraw(Object drawn) {
      return drawn instanceof RandomData2D;
    }
  }

  private final Plot2D plot;
  private RandomData2D drawn = null;
  private float[] xdata = null;
  private float[] ydata = null;

  public PlotRandomData2D() {
    this.plot = new Plot2D();
  }

  @Override
  public boolean synchronize() {
    if (drawn == null)
      return false;
    System.arraycopy(drawn.data, 0, ydata, 0, ydata.length);
    return true;
  }

  @Override
  protected void paint(GC gc) {
    if (drawn == null)
      return;
    plot.clear(gc);
    plot.draw(gc, xdata, ydata);
  }

  @Override
  public void addTimed(Object drawn) {
    this.drawn = (RandomData2D) drawn;
    if (drawn == null)
      return;
    xdata = new float[this.drawn.data.length];
    for (int i = 0; i < xdata.length; i++)
      xdata[i] = i;
    ydata = new float[this.drawn.data.length];
  }

  @Override
  public boolean canTimedAdded() {
    return drawn == null;
  }
}
