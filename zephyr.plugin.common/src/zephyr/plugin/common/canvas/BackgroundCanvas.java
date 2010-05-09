package zephyr.plugin.common.canvas;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import rlpark.plugin.utils.events.Listener;
import rlpark.plugin.utils.time.Chrono;

public class BackgroundCanvas {
  static final boolean drawFPS = false;

  protected class PaintJob extends Job {
    protected double drawingTime = 0;
    protected final Chrono chrono = new Chrono();
    private final Runnable syncDrawImage = new Runnable() {
      @Override
      public void run() {
        if (canvas.isDisposed())
          return;
        canvas.redraw();
        canvas.update();
        drawingTime = chrono.getCurrentMillis();
      }
    };

    public PaintJob() {
      super("BackgroundCanvas.PaintJob");
      setPriority(DECORATE);
    }

    @Override
    public IStatus run(IProgressMonitor monitor) {
      monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
      chrono.start();
      try {
        doPainting(monitor);
        Display.getDefault().asyncExec(syncDrawImage);
      } catch (Exception e) {
        e.printStackTrace();
      }
      monitor.done();
      return Status.OK_STATUS;
    }

    protected void doPainting(IProgressMonitor monitor) {
      drawing = true;
      synchronized (imageBuffer) {
        Image canvasPainted = imageBuffer.canvasPainted();
        if (canvasPainted != null) {
          GC gc = new GC(canvasPainted);
          gc.setClipping(canvasPainted.getBounds());
          painter.paint(gc, monitor);
          gc.dispose();
        }
        imageBuffer.flipImages();
      }
      drawing = false;
    }
  };

  protected final ImageBuffer imageBuffer = new ImageBuffer();
  protected boolean drawing = false;
  protected final Painter painter;
  protected final Canvas canvas;
  private final PaintJob paintJob = new PaintJob();

  public BackgroundCanvas(Painter painter, Canvas canvas) {
    this.canvas = canvas;
    this.painter = painter;
    imageBuffer.onImageAllocated.connect(new Listener<ImageBuffer>() {
      @Override
      public void listen(ImageBuffer eventInfo) {
        repaint();
      }
    });
  }

  public boolean isDrawing() {
    return drawing;
  }

  public void paint(GC gc) {
    synchronized (imageBuffer) {
      Image canvasDisplayed = imageBuffer.canvasDisplayed(canvas);
      gc.drawImage(canvasDisplayed, 0, 0);
    }
    if (drawFPS)
      gc.drawText(String.valueOf(paintJob.drawingTime), 10, 10);
  }

  public void repaint() {
    repaint(false);
  }

  public void repaint(boolean synchronous) {
    if (canvas.isDisposed())
      return;
    if (isDrawing())
      return;
    if (!synchronous) {
      paintJob.schedule();
      return;
    }
    paintJob.doPainting(null);
    canvas.redraw();
    canvas.update();
  }
}
