package zephyr.plugin.core.canvas;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import zephyr.plugin.core.ZephyrPluginCommon;

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
    return bounds.width == canvasSize.x && bounds.height == canvasSize.y;
  }

  public Image image() {
    return backgroundImage;
  }

  public boolean adjustImage(Canvas canvas) {
    if (backgroundImage != null) {
      backgroundImage.dispose();
      backgroundImage = null;
    }
    Point canvasSize = canvas.getSize();
    if (canvasSize.x == 0 || canvasSize.y == 0)
      return false;
    backgroundImage = new Image(canvas.getDisplay(), canvasSize.x, canvasSize.y);
    return true;
  }

  synchronized public GC getGC() {
    if (ZephyrPluginCommon.shuttingDown || backgroundImage == null || Display.getCurrent() != null)
      return null;
    return new GC(backgroundImage);
  }

  public void disposeGC(GC gc) {
    if (gc != null)
      gc.dispose();
  }
}
