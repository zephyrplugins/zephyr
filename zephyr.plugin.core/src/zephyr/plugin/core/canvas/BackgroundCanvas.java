package zephyr.plugin.core.canvas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;

import zephyr.plugin.core.api.synchronization.Chrono;

public class BackgroundCanvas {
  static private ExecutorService executor = Executors.newFixedThreadPool(3, new ThreadFactory() {
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
        if (canvas.isDisposed())
          return;
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
  private final Chrono chrono = new Chrono();
  private boolean showProgress = true;
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

  private void runFromUIThread(Runnable runnable) {
    if (canvas.isDisposed())
      return;
    canvas.getDisplay().syncExec(runnable);
  }

  protected void runPainter() {
    do {
      boolean drawing = true;
      while (drawing || !paintingImage.canvasSizeEquals()) {
        if (!paintingImage.canvasSizeEquals())
          runFromUIThread(allocatePainting);
        if (paintingImage.image() == null)
          return;
        GC gc = paintingImage.getGC();
        chrono.start();
        long drawingTime = 0;
        while (!canvas.isDisposed() && drawing && (drawingTime < 500 || !showProgress)) {
          drawing = !painter.paint(paintingImage.image(), gc);
          drawingTime = chrono.getCurrentMillis();
        }
        paintingImage.disposeGC(gc);
        if (paintingImage.canvasSizeEquals()) {
          imageToCanvas();
          runFromUIThread(refreshCanvas);
        }
        drawing = drawing && !canvas.isDisposed();
      }
      showProgress = false;
    } while (painter.newPaintingRequired());
  }

  private void imageToCanvas() {
    synchronized (canvasImage) {
      GC gc = canvasImage.getGC();
      if (gc != null)
        gc.drawImage(paintingImage.image(), 0, 0);
      canvasImage.disposeGC(gc);
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
        if (!canvasImage.adjustImage(canvas))
          return;
        showProgress();
        drawNewData();
      }
  }

  public void paint(GC gc) {
    updateAllImageSize();
    updateCanvasImage();
    gc.drawImage(canvasImage.image(), 0, 0);
  }

  public void showProgress() {
    showProgress = true;
  }
}
