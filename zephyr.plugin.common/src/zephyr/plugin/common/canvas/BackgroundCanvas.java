package zephyr.plugin.common.canvas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;

public class BackgroundCanvas {
  static private ExecutorService executor = Executors.newFixedThreadPool(4, new ThreadFactory() {
    @Override
    public Thread newThread(Runnable runnable) {
      Thread result = Executors.defaultThreadFactory().newThread(runnable);
      result.setPriority(Thread.MIN_PRIORITY);
      return result;
    }
  });

  class PainterRunnable implements Runnable {
    @Override
    public void run() {
      try {
        runPainter();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  };

  protected final Painter painter;
  protected final Canvas canvas;
  protected final BackgroundImage paintingImage = new BackgroundImage();
  private final BackgroundImage canvasImage = new BackgroundImage();
  private final PainterRunnable painterRunnable = new PainterRunnable();
  private Future<?> future = null;
  private final Runnable refreshCanvas = new Runnable() {
    @Override
    public void run() {
      if (canvas.isDisposed())
        return;
      canvas.redraw();
      canvas.update();
      updateAllImageSize();
    }
  };
  private final Runnable allocatePainting = new Runnable() {
    @Override
    public void run() {
      paintingImage.adjustImage(canvas);
    }
  };

  public BackgroundCanvas(Painter painter, Canvas canvas) {
    this.canvas = canvas;
    this.painter = painter;
  }

  protected void runPainter() {
    boolean drawing = true;
    while (drawing || !paintingImage.canvasSizeEquals()) {
      if (!paintingImage.canvasSizeEquals())
        canvas.getDisplay().syncExec(allocatePainting);
      GC gc = new GC(paintingImage.image());
      drawing = !painter.paint(paintingImage.image(), gc);
      gc.dispose();
      if (paintingImage.canvasSizeEquals()) {
        imageToCanvas();
        canvas.getDisplay().syncExec(refreshCanvas);
      }
    }
  }

  private void imageToCanvas() {
    synchronized (canvasImage) {
      GC gc = new GC(canvasImage.image());
      gc.drawImage(paintingImage.image(), 0, 0);
      gc.dispose();
    }
  }

  synchronized public boolean isDrawing() {
    return future != null && !future.isDone() && !future.isCancelled();
  }

  synchronized public void drawNewData() {
    if (!isDrawing())
      future = executor.submit(painterRunnable);
  }

  protected void updateAllImageSize() {
    canvasImage.updateSize(canvas);
    paintingImage.updateSize(canvas);
  }

  private void updateCanvasImage() {
    if (!canvasImage.canvasSizeEquals())
      synchronized (canvasImage) {
        canvasImage.adjustImage(canvas);
        drawNewData();
      }
  }

  public void paint(GC gc) {
    updateAllImageSize();
    updateCanvasImage();
    gc.drawImage(canvasImage.image(), 0, 0);
  }

  public void dispose() {
    executor.shutdown();
  }
}
