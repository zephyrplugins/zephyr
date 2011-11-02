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

import zephyr.plugin.core.views.ViewWithControl;

public abstract class ForegroundCanvasView<T> extends ClassTypeView<T> implements ViewWithControl {
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
    setToolbar(getViewSite().getActionBars().getToolBarManager());
  }

  abstract protected void paint(GC gc);

  protected void setToolbar(IToolBarManager toolbarManager) {
  }

  public Canvas canvas() {
    return canvas;
  }

  @Override
  public void repaintView() {
    if (canvas == null || canvas.isDisposed())
      return;
    canvas.getDisplay().syncExec(drawOnCanvas);
  }

  @Override
  protected void setLayout() {
    canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
    canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    canvas.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        GC gc = e.gc;
        if (hasBeenSynchronized())
          paint(gc);
        else
          defaultPainting(gc);
      }
    });
  }

  protected void defaultPainting(GC gc) {
    gc.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
  }

  @Override
  protected void unsetLayout() {
    canvas.dispose();
    canvas = null;
  }

  @Override
  public Control control() {
    return canvas;
  }
}
