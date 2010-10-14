package zephyr.plugin.tests.slowdrawing;

import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.core.views.TimedView;

public class SlowDrawingView extends ViewPart implements TimedView {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(SlowDrawingRunnable.class, "zephyr.plugin.tests.slowdrawing");
    }
  }

  private Canvas canvas;
  private final Colors colors = new Colors();
  private final Random random = new Random();

  @Override
  public void createPartControl(Composite parent) {
    parent.setLayout(new FillLayout());
    canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
    canvas.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        paint(e.gc);
      }
    });
  }

  protected void paint(GC gc) {
    Chrono chrono = new Chrono();
    gc.setAntialias(SWT.OFF);
    gc.setBackground(colors.color(gc, new RGB(0, 0, 0)));
    gc.fillRectangle(gc.getClipping());
    gc.setLineWidth(1);
    while (chrono.getTime() < 10) {
      gc.setForeground(colors.color(gc, new RGB(random.nextInt(256), random.nextInt(256), random.nextInt(256))));
      Rectangle clipping = gc.getClipping();
      gc.drawPoint(random.nextInt(clipping.width), random.nextInt(clipping.width));
    }
  }

  @Override
  public void setFocus() {
  }

  @Override
  public boolean synchronize() {
    return true;
  }

  @Override
  public void repaint() {
    System.out.println("Repaint");
    canvas.redraw();
    canvas.update();
  }

  @Override
  public boolean isDisposed() {
    return canvas.isDisposed();
  }

  @Override
  public void addTimed(Object drawn, Object info) {
  }

  @Override
  public boolean canTimedAdded() {
    return true;
  }
}
