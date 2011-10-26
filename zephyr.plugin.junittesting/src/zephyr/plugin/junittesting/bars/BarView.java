package zephyr.plugin.junittesting.bars;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;

import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.views.ViewWithControl;
import zephyr.plugin.core.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.bar2d.Bar2D;

public class BarView extends ForegroundCanvasView<BarRunnable> implements ViewWithControl {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(BarRunnable.class);
    }
  }

  public static final String ViewID = "zephyr.plugin.junittesting.bars.view";

  private BarRunnable current;
  private final Bar2D histogram = new Bar2D();
  private float[] data;

  @Override
  protected void paint(GC gc) {
    histogram.clear(gc);
    if (current == null)
      return;
    histogram.draw(gc, data);
  }

  @Override
  protected void set(BarRunnable current) {
    this.current = current;
    this.data = current.data();
  }

  @Override
  protected void unset() {
    this.current = null;
    this.data = null;
  }

  @Override
  protected boolean synchronize() {
    data = current.data();
    return true;
  }

  @Override
  public Control control() {
    return canvas;
  }
}
