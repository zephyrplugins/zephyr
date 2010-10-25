package zephyr.plugin.core.canvas;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.canvas.Painter.PainterMonitor;
import zephyr.plugin.core.internal.canvas.DoubleBuffer;

public class BackgroundCanvas implements PainterMonitor {
  private final Painter painter;
  final Canvas canvas;
  final DoubleBuffer paintingImage;
  private final Runnable updateForeground = new Runnable() {
    @Override
    public void run() {
      if (canvas.isDisposed())
        return;
      canvas.redraw();
      canvas.update();
    }
  };
  private final List<Overlay> overlays = new LinkedList<Overlay>();
  private boolean showProgress = true;
  private final boolean cancelDrawing = false;
  private final Chrono chrono = new Chrono();

  public BackgroundCanvas(Composite parent, Painter painter) {
    this.canvas = new Canvas(parent, SWT.NO_BACKGROUND);
    this.painter = painter;
    canvas.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        drawForeground(e.gc);
      }
    });
    paintingImage = new DoubleBuffer(canvas);
  }

  public void paint() {
    Image image = paintingImage.acquireImage();
    if (image == null)
      return;
    showProgress = showProgress || !paintingImage.currentImageIsValide();
    GC gc = new GC(image);
    chrono.start();
    painter.paint(this, image, gc);
    showProgress = false;
    paintingImage.releaseImage(gc);
    paintingImage.swap();
    updateForegroundCanvas();
  }

  private void updateForegroundCanvas() {
    if (!canvas.isDisposed())
      canvas.getDisplay().syncExec(updateForeground);
  }

  void drawForeground(GC gc) {
    paintingImage.paintCanvas(gc);
    for (Overlay overlay : overlays)
      overlay.drawOverlay(gc);
  }

  public void addOverlay(Overlay overlay) {
    overlays.add(overlay);
  }

  public void removeOverlay(Overlay overlay) {
    overlays.remove(overlay);
  }

  @Override
  public void painterStep() {
    if (!showProgress || chrono.getTime() < 1.0)
      return;
    paintingImage.transfertImage();
    updateForegroundCanvas();
    chrono.start();
  }

  @Override
  public boolean isCanceled() {
    return cancelDrawing || !paintingImage.currentImageIsValide();
  }

  public void dispose() {
    paintingImage.dispose();
  }

  public Control canvas() {
    return canvas;
  }
}
