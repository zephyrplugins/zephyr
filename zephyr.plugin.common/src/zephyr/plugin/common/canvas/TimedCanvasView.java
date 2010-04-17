package zephyr.plugin.common.canvas;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.common.views.TimedView;

public abstract class TimedCanvasView extends AbstractCanvasView implements TimedView {

  protected Object drawn;
  private final Class<?> drawnClass;

  public TimedCanvasView(Class<?> drawed) {
    this.drawnClass = drawed;
    drawn = null;
  }

  @Override
  protected void paint(GC gc) {
    if (drawn != null)
      paintTimed(gc);
  }

  @Override
  public boolean canDraw(Object drawn) {
    return drawnClass.isInstance(drawn);
  }

  @Override
  public void setTimed(Object drawn) {
    this.drawn = drawn;
  }

  @Override
  public Object drawn() {
    return drawn;
  }

  abstract protected void paintTimed(GC gc);
}
