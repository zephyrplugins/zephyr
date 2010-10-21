package zephyr.plugin.core.canvas;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.canvas.Painter.PainterMonitor;
import zephyr.plugin.core.internal.canvas.BackgroundImage;

public class BackgroundCanvas implements PainterMonitor {
  private final Painter painter;
  final Canvas canvas;
  final BackgroundImage paintingImage;
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
    this.canvas = new Canvas(parent, SWT.NONE);
    this.painter = painter;
    canvas.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        drawForeground(e.gc);
      }
    });
    paintingImage = new BackgroundImage(canvas);
  }

  public void paint() {
    showProgress = showProgress || !paintingImage.imageMatchCanvas();
    GC gc = paintingImage.getGC();
    if (gc == null)
      return;
    chrono.start();
    painter.paint(this, paintingImage.image(), gc);
    showProgress = false;
    paintingImage.disposeGC(gc);
    updateForegroundCanvas();
  }

  private void updateForegroundCanvas() {
    canvas.getDisplay().syncExec(updateForeground);
  }

  void drawForeground(GC gc) {
    if (paintingImage.image() == null)
      return;
    gc.drawImage(paintingImage.image(), 0, 0);
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
    updateForegroundCanvas();
    chrono.start();
  }

  @Override
  public boolean isCanceled() {
    return cancelDrawing || !paintingImage.imageMatchCanvas();
  }

  public void dispose() {
    canvas.dispose();
    paintingImage.dispose();
  }

  public Control canvas() {
    return canvas;
  }
}
