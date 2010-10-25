package zephyr.plugin.tests.slowdrawing;

import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.canvas.BackgroundCanvas;
import zephyr.plugin.core.canvas.Painter;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.core.views.TimedView;

public class SlowDrawingView extends ViewPart implements TimedView, Painter {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(SlowDrawingRunnable.class, "zephyr.plugin.tests.slowdrawing");
    }
  }

  private BackgroundCanvas backgroundCanvas;
  private final Colors colors = new Colors();
  private final Random random = new Random();

  @Override
  public void createPartControl(Composite parent) {
    parent.setLayout(new FillLayout());
    backgroundCanvas = new BackgroundCanvas(parent, this);
  }

  @Override
  public void paint(PainterMonitor monitor, Image image, GC gc) {
    gc.setAntialias(SWT.OFF);
    gc.setBackground(colors.color(gc, new RGB(0, 0, 0)));
    gc.fillRectangle(gc.getClipping());
    gc.setLineWidth(1);
    Chrono chrono = new Chrono();
    while (chrono.getTime() < 10) {
      gc.setForeground(colors.color(gc, new RGB(random.nextInt(256), random.nextInt(256), random.nextInt(256))));
      Rectangle clipping = gc.getClipping();
      gc.drawPoint(random.nextInt(clipping.width), random.nextInt(clipping.width));
      if (monitor.isCanceled())
        return;
      monitor.painterStep();
    }
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void synchronize() {
  }

  @Override
  public void repaint() {
    backgroundCanvas.paint();
  }

  @Override
  public void addTimed(Clock clock, Object drawn, Object info) {
  }

  @Override
  public boolean canAddTimed() {
    return true;
  }
}
