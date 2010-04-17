package zephyr.plugin.common.canvas;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;

import rlpark.plugin.utils.events.Signal;

public class ImageBuffer {
  public final Signal<ImageBuffer> onImageAllocated = new Signal<ImageBuffer>();
  protected Image canvasPainting = null;
  protected Image canvasDisplayed = null;
  private Point canvasSize;

  private boolean canvasSizeEquals(Image image) {
    if (image == null)
      return false;
    return (image.getBounds().width == canvasSize.x && image.getBounds().height == canvasSize.y);
  }

  private void allocateDisplayCanvas(Canvas canvas) {
    if (canvasDisplayed != null)
      canvasDisplayed.dispose();
    canvasDisplayed = new Image(canvas.getDisplay(), canvasSize.x, canvasSize.y);
    onImageAllocated.fire(this);
  }

  synchronized protected void flipImages() {
    Image buffer = canvasDisplayed;
    canvasDisplayed = canvasPainting;
    canvasPainting = buffer;
  }

  synchronized public Image canvasDisplayed(final Canvas canvas) {
    canvasSize = canvas.getSize();
    if (!canvasSizeEquals(canvasDisplayed))
      allocateDisplayCanvas(canvas);
    return canvasDisplayed;
  }

  synchronized public Image canvasPainted() {
    if (!canvasSizeEquals(canvasPainting))
      return null;
    return canvasPainting;
  }
}
