package zephyr.plugin.core.views.helpers;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class ForegroundCanvasView<T> extends ClassTypeView<T> {
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
    parent.setLayout(gridLayout);
    canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
    canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    canvas.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        GC gc = e.gc;
        if (instance.current() != null && hasBeenSynchronized())
          synchronizedPaint(gc);
        else
          defaultPainting(gc);
      }
    });
    setToolbar(getViewSite().getActionBars().getToolBarManager());
  }

  protected void synchronizedPaint(GC gc) {
    if (!viewLock.acquire())
      return;
    paint(gc);
    viewLock.release();
  }

  abstract protected void paint(GC gc);

  protected void setToolbar(IToolBarManager toolbarManager) {
  }

  public Canvas canvas() {
    return canvas;
  }

  @Override
  public void repaint() {
    if (!canvas.isDisposed())
      canvas.getDisplay().syncExec(drawOnCanvas);
  }

  protected void defaultPainting(GC gc) {
    gc.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    gc.fillRectangle(gc.getClipping());
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
}
