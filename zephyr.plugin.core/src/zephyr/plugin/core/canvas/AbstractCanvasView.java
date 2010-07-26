package zephyr.plugin.core.canvas;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.views.SyncView;

public abstract class AbstractCanvasView extends ViewPart implements SyncView {
  protected Canvas canvas = null;
  protected Composite parent = null;

  public AbstractCanvasView() {
  }

  @Override
  public void createPartControl(final Composite parent) {
    this.parent = parent;
    GridLayout gridLayout = new GridLayout(1, false);
    parent.setLayout(gridLayout);
    canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
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
    canvas.redraw();
    canvas.update();
  }

  protected void setViewName(final String viewName) {
    setPartName(viewName);
    Display.getDefault().syncExec(new Runnable() {
      @SuppressWarnings("synthetic-access")
      @Override
      public void run() {
        firePropertyChange(org.eclipse.ui.IWorkbenchPart.PROP_TITLE);
        parent.redraw();
        parent.update();
      }
    });
  }

  @Override
  public boolean isDisposed() {
    return canvas.isDisposed();
  }
}
