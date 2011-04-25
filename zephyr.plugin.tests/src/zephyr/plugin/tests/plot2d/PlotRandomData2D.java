package zephyr.plugin.tests.plot2d;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.plot2d.Data2D;
import zephyr.plugin.plotting.plot2d.Plot2D;


public class PlotRandomData2D extends ForegroundCanvasView<RandomData2D> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(RandomData2D.class);
    }
  }

  private final Plot2D plot;
  private RandomData2D drawn = null;
  private Data2D data;

  public PlotRandomData2D() {
    plot = new Plot2D();
  }

  @Override
  public boolean synchronize() {
    System.arraycopy(drawn.data, 0, data.ydata, 0, data.nbPoints);
    return true;
  }

  @Override
  protected void paint(GC gc) {
    plot.clear(gc);
    if (drawn == null || data == null)
      return;
    plot.draw(gc, data);
    gc.drawString(String.valueOf(data.ydata[1]), 10, 10);
    gc.drawString(String.valueOf(data.ydata[2]), 10, 30);
  }

  @Override
  public void unset() {
    this.drawn = null;
  }

  @Override
  protected void set(RandomData2D current) {
    drawn = current;
    data = new Data2D("Random", this.drawn.data.length);
    for (int i = 0; i < data.nbPoints; i++)
      data.xdata[i] = i;
  }

  @Override
  protected Class<?> classSupported() {
    return RandomData2D.class;
  }
}
