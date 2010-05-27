package zephyr.plugin.common.canvas;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class BackgroundImage {
  protected Image backgroundImage = null;
  private Point canvasSize = null;

  public void updateSize(Canvas canvas) {
    canvasSize = canvas.getSize();
  }

  public boolean canvasSizeEquals() {
    if (backgroundImage == null)
      return false;
    Rectangle bounds = backgroundImage.getBounds();
    return (bounds.width == canvasSize.x && bounds.height == canvasSize.y);
  }

  public Image image() {
    return backgroundImage;
  }

  public void adjustImage(Canvas canvas) {
    Point canvasSize = canvas.getSize();
    if (backgroundImage != null)
      backgroundImage.dispose();
    backgroundImage = new Image(canvas.getDisplay(), canvasSize.x, canvasSize.y);
  }

  synchronized public GC getGC() {
    if (backgroundImage == null || Display.getCurrent() != null)
      return null;
    return new GC(backgroundImage);
  }
}
