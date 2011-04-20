package zephyr.plugin.tests.plot2d;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.canvas.AbstractCanvasView;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.views.TimedView;
import zephyr.plugin.plotting.plot2d.Data2D;
import zephyr.plugin.plotting.plot2d.Plot2D;


public class PlotRandomData2D extends AbstractCanvasView implements TimedView {
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
  public boolean synchronize(Clock clock) {
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
  public boolean[] provide(CodeNode[] codeNode) {
    if (this.drawn != null)
      return new boolean[] { false };
    this.drawn = (RandomData2D) ((ClassNode) codeNode[0]).instance();
    data = new Data2D("Random", this.drawn.data.length);
    for (int i = 0; i < data.nbPoints; i++)
      data.xdata[i] = i;
    return new boolean[] { true };
  }

  @Override
  public void removeClock(Clock clock) {
    dispose();
  }
}
