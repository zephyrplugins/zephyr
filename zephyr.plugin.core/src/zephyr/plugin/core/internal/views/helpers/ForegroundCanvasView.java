package zephyr.plugin.core.internal.views.helpers;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import zephyr.plugin.core.internal.views.helpers.ScreenShotAction.Shotable;

public abstract class ForegroundCanvasView<T> extends ClassTypeView<T> implements Shotable {
  protected Canvas canvas = null;
  protected Runnable drawOnCanvas = new Runnable() {
    @Override
    public void run() {
      if (canvas.isDisposed())
        return;
      canvas.redraw();
      canvas.update();
    }
  };

  @Override
  public void createPartControl(final Composite parent) {
    super.createPartControl(parent);
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    parent.setLayout(gridLayout);
    canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
    canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    canvas.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        if (!viewLock.acquire())
          return;
        GC gc = e.gc;
        if (instance.current() != null && hasBeenSynchronized())
          paint(gc);
        else
          defaultPainting(gc);
        viewLock.release();
      }
    });
    setToolbar(getViewSite().getActionBars().getToolBarManager());
  }

  abstract protected void paint(GC gc);

  protected void setToolbar(IToolBarManager toolbarManager) {
  }

  public Canvas canvas() {
    return canvas;
  }

  @Override
  public void repaint() {
    if (canvas == null || canvas.isDisposed())
      return;
    canvas.getDisplay().syncExec(drawOnCanvas);
  }

  @Override
  protected void setLayout() {
    setViewName();
  }

  @Override
  protected void unsetLayout() {
    setViewName();
  }

  @Override
  public Control control() {
    return canvas;
  }

  @Override
  public Image takeScreenshot() {
    if (!viewLock.acquire())
      return null;
    Image screenshot = takeScreenshotUnprotected();
    viewLock.release();
    return screenshot;
  }

  private Image takeScreenshotUnprotected() {
    if (canvas == null)
      return null;
    Image image = new Image(canvas.getDisplay(), canvas.getBounds());
    GC gc = new GC(image);
    paint(gc);
    return image;
  }
}
