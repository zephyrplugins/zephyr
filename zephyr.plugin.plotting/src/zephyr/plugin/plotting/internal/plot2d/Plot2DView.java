package zephyr.plugin.plotting.internal.plot2d;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import zephyr.plugin.core.internal.views.helpers.BackgroundCanvasView;
import zephyr.plugin.plotting.internal.actions.CenterPlotAction;
import zephyr.plugin.plotting.internal.actions.CenterPlotAction.ViewCenterable;
import zephyr.plugin.plotting.internal.mousesearch.MouseSearch;

abstract public class Plot2DView<T> extends BackgroundCanvasView<T> implements ViewCenterable {
  protected final Plot2D plot = new Plot2D();
  private MouseSearch mouseSearch;

  @Override
  public void center() {
    plot.resetAxes();
  }

  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    Control canvas = backgroundCanvas.canvas();
    mouseSearch = new MouseSearch(plot.dataBuffer(), canvas);
    backgroundCanvas.addOverlay(mouseSearch);
    setupToolbar(getViewSite().getActionBars().getToolBarManager());
    Composite settingBar = new Composite(parent, SWT.NONE);
    RowLayout settingBarLayout = new RowLayout();
    settingBarLayout.marginWidth = 0;
    settingBarLayout.marginHeight = 0;
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
  public void unsetLayout() {
    plot.clearData();
  }
}
