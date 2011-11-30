package zephyr.plugin.core.views.helpers;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import zephyr.plugin.core.canvas.BackgroundCanvas;
import zephyr.plugin.core.canvas.Painter;

public abstract class BackgroundCanvasView<T> extends ClassTypeView<T> implements Painter {
  protected BackgroundCanvas backgroundCanvas;

  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    parent.setLayout(gridLayout);
    backgroundCanvas = new BackgroundCanvas(parent, this);
    backgroundCanvas.setFillLayout();
    setToolbar(getViewSite().getActionBars().getToolBarManager());
  }

  protected void setToolbar(IToolBarManager toolBarManager) {
  }

  @Override
  public void repaint() {
    backgroundCanvas.paint();
  }

  @Override
  public void paint(PainterMonitor painterListener, Image image, GC gc) {
    if (instance.current() != null && hasBeenSynchronized())
      synchronizedPaint(painterListener, gc);
    else
      defaultPainting(image, gc);

  }

  private void synchronizedPaint(PainterMonitor painterListener, GC gc) {
    if (!viewLock.acquire())
      return;
    paint(painterListener, gc);
    viewLock.release();
  }

  protected void defaultPainting(Image image, final GC gc) {
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        gc.fillRectangle(gc.getClipping());
      }
    });
  }

  abstract protected void paint(PainterMonitor painterListener, GC gc);
}
