package zephyr.plugin.core.helpers;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.core.api.synchronization.Clock;
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
  public boolean addTimed(Clock clock, Object drawn, Object info) {
    if (this.drawn != null)
      return false;
    this.drawn = drawn;
    return true;
  }

  abstract protected void paintTimed(GC gc);
}
