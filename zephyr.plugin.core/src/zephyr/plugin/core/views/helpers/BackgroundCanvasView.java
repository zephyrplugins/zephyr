package zephyr.plugin.core.views.helpers;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

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
    if (!viewLock.acquire())
      return;
    if (instance.current() != null && hasBeenSynchronized())
      paint(painterListener, gc);
    else
      defaultPainting(gc);
    viewLock.release();
  }

  abstract protected void paint(PainterMonitor painterListener, GC gc);
}
