package zephyr.plugin.tests.plot2d;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.canvas.AbstractCanvasView;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.views.TimedView;
import zephyr.plugin.plotting.plot2d.Data2D;
import zephyr.plugin.plotting.plot2d.Plot2D;


public class PlotRandomData2D extends AbstractCanvasView implements TimedView {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(RandomData2D.class, "zephyr.plugin.tests.plot2d.plotrandomplot2d");
    }
  }

  private final Plot2D plot;
  private RandomData2D drawn = null;
  private Data2D data;

  public PlotRandomData2D() {
    this.plot = new Plot2D();
  }

  @Override
  public void synchronize() {
    System.arraycopy(drawn.data, 0, data.ydata, 0, data.nbPoints);
  }

  @Override
  protected void paint(GC gc) {
    plot.clear(gc);
    plot.draw(gc, data);
    gc.drawString(String.valueOf(data.ydata[1]), 10, 10);
    gc.drawString(String.valueOf(data.ydata[2]), 10, 30);
  }

  @Override
  public void addTimed(Clock clock, Object drawn, Object info) {
    this.drawn = (RandomData2D) drawn;
    if (drawn == null)
      return;
    data = new Data2D("Random", this.drawn.data.length);
    for (int i = 0; i < data.nbPoints; i++)
      data.xdata[i] = i;
  }

  @Override
  public boolean canAddTimed() {
    return drawn == null;
  }

  @Override
  protected boolean isSetup() {
    return drawn != null;
  }
}
