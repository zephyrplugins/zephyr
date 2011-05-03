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
    canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
    GridLayout gridLayout = new GridLayout(1, false);
    canvas.getParent().setLayout(gridLayout);
    canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    canvas.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        paint(e.gc);
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
  public void repaintView() {
    if (canvas.isDisposed())
      return;
    canvas.getDisplay().syncExec(drawOnCanvas);
  }

  @Override
  public void unset() {
  }
}
