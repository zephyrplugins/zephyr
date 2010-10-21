package zephyr.plugin.core.canvas;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import zephyr.ZephyrSync;
import zephyr.plugin.core.views.SyncView;

public abstract class AbstractCanvasView extends ViewPart implements SyncView {
  protected Canvas canvas = null;
  protected Composite parent = null;
  protected Runnable drawOnCanvas = new Runnable() {
    @Override
    public void run() {
      if (canvas.isDisposed())
        return;
      canvas.redraw();
      canvas.update();
    }
  };

  public AbstractCanvasView() {
  }

  @Override
  public void createPartControl(final Composite parent) {
    this.parent = parent;
    canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
    Views.setLayoutData(canvas);
    canvas.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        if (!isSetup())
          return;
        paint(e.gc);
      }
    });
    setToolbar(getViewSite().getActionBars().getToolBarManager());
  }

  abstract protected boolean isSetup();

  abstract protected void paint(GC gc);

  @SuppressWarnings("unused")
  protected void setToolbar(IToolBarManager toolbarManager) {
  }

  public Canvas canvas() {
    return canvas;
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void repaint() {
    if (!isSetup())
      return;
    canvas.getDisplay().syncExec(drawOnCanvas);
  }

  protected void setViewName(final String viewName) {
    Display.getDefault().syncExec(new Runnable() {
      @SuppressWarnings("synthetic-access")
      @Override
      public void run() {
        setPartName(viewName);
        firePropertyChange(org.eclipse.ui.IWorkbenchPart.PROP_TITLE);
        parent.redraw();
        parent.update();
      }
    });
  }

  @Override
  public void dispose() {
    ZephyrSync.disposeView(this);
    super.dispose();
  }
}
