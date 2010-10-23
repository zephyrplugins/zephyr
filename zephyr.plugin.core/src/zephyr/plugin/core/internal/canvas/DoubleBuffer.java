package zephyr.plugin.core.internal.canvas;

import java.util.concurrent.Semaphore;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

public class DoubleBuffer implements ControlListener {

  private final Canvas canvas;
  protected Rectangle canvasRectangle = null;
  protected Rectangle backgroundRectangle = null;
  private final Semaphore drawingSemaphore = new Semaphore(1);
  private Image backgroundImage = null;
  private Image foregroundImage = null;
  private boolean disposed = false;

  public DoubleBuffer(Canvas canvas) {
    this.canvas = canvas;
    controlResized(null);
    canvas.addControlListener(this);
  }

  public boolean currentImageIsValide() {
    return checkBackgroundImage();
  }

  private boolean checkBackgroundImage() {
    return backgroundImage != null && backgroundImage.getBounds().equals(canvasRectangle);
  }

  public Image acquireImage() {
    if (disposed)
      return null;
    acquireDrawingSemaphore();
    if (!checkBackgroundImage()) {
      if (backgroundImage != null)
        backgroundImage.dispose();
      if (nullCanvasArea()) {
        releaseDrawingSemaphore();
        return null;
      }
      backgroundImage = new Image(canvas.getDisplay(), canvasRectangle.width, canvasRectangle.height);
    }
    return backgroundImage;
  }

  private boolean nullCanvasArea() {
    return canvasRectangle.width == 0 || canvasRectangle.height == 0;
  }

  public void releaseImage(GC gc) {
    if (gc == null)
      return;
    gc.dispose();
    releaseDrawingSemaphore();
  }

  @Override
  public void controlMoved(ControlEvent e) {
  }

  @Override
  public void controlResized(ControlEvent e) {
    Point canvasSize = canvas.getSize();
    canvasRectangle = new Rectangle(0, 0, canvasSize.x, canvasSize.y);
  }

  synchronized public void dispose() {
    disposed = true;
    acquireDrawingSemaphore();
    if (backgroundImage != null)
      backgroundImage.dispose();
    releaseDrawingSemaphore();
    if (foregroundImage != null)
      foregroundImage.dispose();
  }

  synchronized public void swap() {
    acquireDrawingSemaphore();
    Image buffer = foregroundImage;
    foregroundImage = backgroundImage;
    backgroundImage = buffer;
    releaseDrawingSemaphore();
  }

  private void releaseDrawingSemaphore() {
    drawingSemaphore.release();
  }

  private void acquireDrawingSemaphore() {
    try {
      drawingSemaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  synchronized public void transfertImage() {
    assert drawingSemaphore.availablePermits() == 0;
    if (foregroundImage == null) {
      if (nullCanvasArea() || canvas.isDisposed())
        return;
      foregroundImage = new Image(canvas.getDisplay(), canvasRectangle.width, canvasRectangle.height);
    }
    GC gc = new GC(foregroundImage);
    gc.drawImage(backgroundImage, 0, 0);
    gc.dispose();
  }

  synchronized public void paintCanvas(GC gc) {
    if (foregroundImage == null || canvas.isDisposed())
      return;
    gc.drawImage(foregroundImage, 0, 0);
  }
}
