package zephyr.plugin.plotting.plot2d;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.canvas.BackgroundCanvas;
import zephyr.plugin.core.canvas.Painter;
import zephyr.plugin.core.views.SyncView;
import zephyr.plugin.plotting.actions.CenterPlotAction;
import zephyr.plugin.plotting.actions.CenterPlotAction.ViewCenterable;
import zephyr.plugin.plotting.mousesearch.MouseSearch;

abstract public class Plot2DView extends ViewPart implements SyncView, ViewCenterable, Painter {

  private BackgroundCanvas backgroundCanvas;
  protected final Plot2D plot = new Plot2D();
  private Composite parent;
  private MouseSearch mouseSearch;

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
    GridLayout gridLayout = new GridLayout(1, false);
    parent.setLayout(gridLayout);
    backgroundCanvas = new BackgroundCanvas(parent, this);
    Control canvas = backgroundCanvas.canvas();
    canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    mouseSearch = new MouseSearch(plot, canvas);
    backgroundCanvas.addOverlay(mouseSearch);
    setupToolbar(getViewSite().getActionBars().getToolBarManager());
    Composite settingBar = new Composite(parent, SWT.NONE);
    RowLayout settingBarLayout = new RowLayout();
    settingBarLayout.center = true;
    settingBar.setLayout(settingBarLayout);
    setSettingBar(settingBar);
  }

  protected void setSettingBar(Composite settingBar) {
    mouseSearch.createLabelControl(settingBar);
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
