package zephyr.plugin.plotting.privates.view;

import java.awt.geom.Point2D;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.viewable.ContinuousFunction2D;
import zephyr.plugin.core.api.viewable.PositionFunction2D;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;
import zephyr.plugin.core.internal.views.helpers.ScreenShotAction;
import zephyr.plugin.plotting.internal.axes.Axes;
import zephyr.plugin.plotting.internal.heatmap.ColorMapAction;
import zephyr.plugin.plotting.internal.heatmap.Function2DDrawer;
import zephyr.plugin.plotting.internal.heatmap.FunctionSampler;
import zephyr.plugin.plotting.internal.heatmap.MapData;

public class HeatMapView extends ForegroundCanvasView<ContinuousFunction2D> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(ContinuousFunction2D.class);
    }

    @Override
    protected boolean isInstanceSupported(Object instance) {
      return isSupported(instance);
    }
  }

  static private final int PositionSize = 4;
  private final Colors colors = new Colors();
  private final Function2DDrawer valueFunctionDrawer = new Function2DDrawer(colors);
  private final Axes axes = new Axes();
  private final ColorMapAction colorMapAction = new ColorMapAction(this, valueFunctionDrawer);
  private Point2D position;
  private MapData valueFunctionData;
  private FunctionSampler valueFunctionSampler;

  @Override
  protected void paint(GC gc) {
    axes.updateScaling(gc.getClipping());
    valueFunctionDrawer.paint(gc, canvas, valueFunctionData, true);
    if (position != null)
      drawPosition(gc);
  }

  private void drawPosition(GC gc) {
    gc.setBackground(colors.color(gc, valueFunctionDrawer.spriteColor()));
    gc.fillOval(axes.toGX(position.getX()) - (PositionSize / 2), axes.toGY(position.getY()) - (PositionSize / 2),
                PositionSize, PositionSize);
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return isSupported(instance);
  }

  static boolean isSupported(Object instance) {
    return (instance instanceof ContinuousFunction2D);
  }

  @Override
  protected boolean synchronize(ContinuousFunction2D current) {
    valueFunctionSampler.updateData(valueFunctionData);
    if (current instanceof PositionFunction2D)
      position = ((PositionFunction2D) current).position();
    return true;
  }

  @Override
  public void onInstanceSet(Clock clock, ContinuousFunction2D function) {
    super.onInstanceSet(clock, function);
    valueFunctionData = new MapData(200);
    valueFunctionSampler = new FunctionSampler(function);
    updateAxes(function);
  }

  private void updateAxes(ContinuousFunction2D function) {
    axes.x.reset();
    axes.x.update(function.minX());
    axes.x.update(function.maxX());
    axes.y.reset();
    axes.y.update(function.minY());
    axes.y.update(function.maxY());
  }

  @Override
  protected void setToolbar(IToolBarManager toolbarManager) {
    toolbarManager.add(new ScreenShotAction(this));
    toolbarManager.add(colorMapAction);
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    colorMapAction.init(memento);
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    colorMapAction.saveState(memento);
  }

  @Override
  public void onInstanceUnset(Clock clock) {
    super.onInstanceUnset(clock);
    valueFunctionDrawer.unset();
  }
}
