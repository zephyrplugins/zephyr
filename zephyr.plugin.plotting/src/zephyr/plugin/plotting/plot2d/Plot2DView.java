package zephyr.plugin.plotting.plot2d;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.canvas.BackgroundCanvas;
import zephyr.plugin.core.canvas.Painter;
import zephyr.plugin.core.canvas.Views;
import zephyr.plugin.core.views.SyncView;
import zephyr.plugin.plotting.actions.CenterPlotAction;
import zephyr.plugin.plotting.actions.CenterPlotAction.ViewCenterable;

abstract public class Plot2DView extends ViewPart implements SyncView, ViewCenterable, Painter {

  private BackgroundCanvas backgroundCanvas;
  protected final Plot2D plot = new Plot2D();
  private Composite parent;

  @Override
  public void repaint() {
    backgroundCanvas.paint();
  }

  @Override
  public void center() {
    plot.resetAxes();
  }

  @Override
  public void createPartControl(Composite parent) {
    this.parent = parent;
    backgroundCanvas = new BackgroundCanvas(parent, this);
    Views.setLayoutData(backgroundCanvas.canvas());
    setupToolbar(getViewSite().getActionBars().getToolBarManager());
  }

  protected void setupToolbar(IToolBarManager toolBarManager) {
    toolBarManager.add(new CenterPlotAction(this));
  }

  @Override
  public void setFocus() {
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
}
