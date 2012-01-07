package zephyr.plugin.junittesting.bars;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.bar2d.Bar2D;

public class BarView extends ForegroundCanvasView<BarModel> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(BarModel.class);
    }
  }

  public static final String ViewID = "zephyr.plugin.junittesting.bars.view";

  private final Bar2D histogram = new Bar2D();
  private double[] data;

  @Override
  protected void paint(GC gc) {
    histogram.clear(gc);
    histogram.draw(gc, data);
  }

  @Override
  protected boolean synchronize() {
    data = instance.current().data();
    return true;
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return BarModel.class.isInstance(instance);
  }
}
