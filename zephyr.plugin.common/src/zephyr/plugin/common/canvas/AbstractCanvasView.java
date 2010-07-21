package zephyr.plugin.common.canvas;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.common.views.SyncView;

public abstract class AbstractCanvasView extends ViewPart implements SyncView {
  protected Canvas canvas = null;

  public AbstractCanvasView() {
  }

  @Override
  public void createPartControl(final Composite parent) {
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

  @Override
  public boolean isDisposed() {
    return canvas.isDisposed();
  }
}
