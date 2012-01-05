package zephyr.plugin.core.canvas;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import zephyr.ZephyrSync;
import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.canvas.Painter.PainterMonitor;
import zephyr.plugin.core.internal.canvas.DoubleBuffer;
import zephyr.plugin.core.views.SyncView;

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
    }
  };
  private final List<Overlay> overlays = new LinkedList<Overlay>();
  private boolean showProgress = true;
  private final Chrono chrono = new Chrono();

  public BackgroundCanvas(Composite parent, Painter painter) {
    canvas = new Canvas(parent, SWT.NO_BACKGROUND);
    this.painter = painter;
    canvas.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        drawForeground(e.gc);
      }
    });
    paintingImage = new DoubleBuffer(canvas);
  }

  public boolean draw() {
    Image image = paintingImage.acquireImage();
    if (image == null)
      return false;
    showProgress = showProgress || !paintingImage.currentImageIsValide();
    GC gc = new GC(image);
    chrono.start();
    if (!gc.getClipping().isEmpty())
      painter.paint(this, image, gc);
    showProgress = false;
    paintingImage.releaseImage(gc);
    paintingImage.swap();
    return true;
  }

  public void paint() {
    if (draw())
      updateForegroundCanvas();
  }

  public void updateForegroundCanvas() {
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
    if (!showProgress || chrono.getCurrentChrono() < 1.0)
      return;
    paintingImage.transfertImage();
    updateForegroundCanvas();
    chrono.start();
  }

  @Override
  public boolean isCanceled() {
    return !paintingImage.currentImageIsValide();
  }

  public void dispose() {
    canvas.dispose();
    paintingImage.dispose();
  }

  public Control canvas() {
    return canvas;
  }

  public void listenControlEvent(final SyncView syncView) {
    canvas.addControlListener(new ControlListener() {
      @Override
      public void controlResized(ControlEvent e) {
        ZephyrSync.submitView(syncView);
      }

      @Override
      public void controlMoved(ControlEvent e) {
        ZephyrSync.submitView(syncView);
      }
    });
  }

  public void setFillLayout() {
    canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
  }
}
