package zephyr.plugin.core.internal.canvas;

import java.util.concurrent.Semaphore;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

public class BackgroundImage implements ControlListener {
  protected Image backgroundImage = null;
  protected Rectangle canvasRectangle = null;
  private final Canvas canvas;
  private final Semaphore semaphore = new Semaphore(1);

  public BackgroundImage(Canvas canvas) {
    this.canvas = canvas;
    updateCanvasSize();
    canvas.addControlListener(this);
  }

  public Image image() {
    return backgroundImage;
  }

  private void updateCanvasSize() {
    Point canvasSize = canvas.getSize();
    canvasRectangle = new Rectangle(0, 0, canvasSize.x, canvasSize.y);
  }

  private void adjustImageIFN() {
    if (backgroundImage != null && imageMatchCanvas())
      return;
    if (backgroundImage != null) {
      backgroundImage.dispose();
      backgroundImage = null;
    }
    if (canvasRectangle.width == 0 || canvasRectangle.height == 0)
      return;
    backgroundImage = new Image(canvas.getDisplay(), canvasRectangle.width, canvasRectangle.height);
  }

  public boolean imageMatchCanvas() {
    return backgroundImage.getBounds().equals(canvasRectangle);
  }

  synchronized public GC getGC() {
    if (canvas.isDisposed())
      return null;
    adjustImageIFN();
    if (backgroundImage == null)
      return null;
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return new GC(backgroundImage);
  }

  public void disposeGC(GC gc) {
    gc.dispose();
    semaphore.release();
  }

  @Override
  public void controlMoved(ControlEvent e) {
  }

  @Override
  public void controlResized(ControlEvent e) {
    updateCanvasSize();
  }

  public void dispose() {
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (backgroundImage != null) {
      backgroundImage.dispose();
      backgroundImage = null;
    }
    semaphore.release();
  }
}
