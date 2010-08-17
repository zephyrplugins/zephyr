package zephyr.plugin.core.helpers;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.core.canvas.AbstractCanvasView;
import zephyr.plugin.core.views.TimedView;

public abstract class TimedCanvasView extends AbstractCanvasView implements TimedView {
  protected Object drawn = null;

  @Override
  protected void paint(GC gc) {
    if (drawn != null)
      paintTimed(gc);
  }

  @Override
  public void addTimed(String info, Object drawn) {
    this.drawn = drawn;
  }

  @Override
  public boolean canTimedAdded() {
    return drawn == null;
  }

  abstract protected void paintTimed(GC gc);
}
