package zephyr.plugin.plotting.privates.view;

import java.awt.geom.Point2D;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.viewable.ContinuousFunction2D;
import zephyr.plugin.core.api.viewable.PositionFunction2D;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.helpers.CodeNodeToInstance;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;
import zephyr.plugin.core.internal.views.helpers.ScreenShotAction;
import zephyr.plugin.plotting.internal.actions.EnableScaleAction;
import zephyr.plugin.plotting.internal.actions.SynchronizeAction;
import zephyr.plugin.plotting.internal.axes.Axes;
import zephyr.plugin.plotting.internal.heatmap.ColorMapAction;
import zephyr.plugin.plotting.internal.heatmap.ContinuousFunctionSampler;
import zephyr.plugin.plotting.internal.heatmap.Function2DBufferedDrawer;
import zephyr.plugin.plotting.internal.heatmap.MapData;
import zephyr.plugin.plotting.internal.heatmap.MapDataUpdater;
import zephyr.plugin.plotting.internal.mousesearch.MouseSearch;
import zephyr.plugin.plotting.internal.mousesearch.MouseSearchable;
import zephyr.plugin.plotting.internal.mousesearch.RequestResult;

public class HeatMapView extends ForegroundCanvasView<MapDataUpdater> implements MouseSearchable {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(ContinuousFunction2D.class, MapDataUpdater.class);
    }

    @Override
    protected boolean isInstanceSupported(Object instance) {
      return isSupported(instance);
    }
  }

  static private final CodeNodeToInstance<MapDataUpdater> codeNodeToInstance = new CodeNodeToInstance<MapDataUpdater>() {
    @Override
    public MapDataUpdater toInstance(ClassNode codeNode) {
      Object instance = codeNode.instance();
      if (instance instanceof ContinuousFunction2D)
        instance = new ContinuousFunctionSampler((ContinuousFunction2D) instance);
      return (MapDataUpdater) instance;
    }
  };

  static private final int PositionSize = 4;
  private final Colors colors = new Colors();
  private final Function2DBufferedDrawer drawer = new Function2DBufferedDrawer(colors);
  private final Axes axes = new Axes();
  private final ColorMapAction colorMapAction = new ColorMapAction(this, drawer);
  private final EnableScaleAction centerAction = new EnableScaleAction();
  private final SynchronizeAction synchronizeAction = new SynchronizeAction();
  private Point2D position;
  MapData data;
  MapDataUpdater sampler;

  public HeatMapView() {
    super(codeNodeToInstance);
  }

  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    new MouseSearch(this, canvas);
  }

  @Override
  synchronized protected void paint(GC gc) {
    synchronized (data) {
      axes.updateScaling(gc.getClipping());
      drawer.paint(gc, canvas, data, true);
    }
    if (position != null)
      drawPosition(gc);
  }

  private void drawPosition(GC gc) {
    gc.setBackground(colors.color(gc, drawer.spriteColor()));
    gc.fillOval(axes.toGX(position.getX()) - (PositionSize / 2), axes.toGY(position.getY()) - (PositionSize / 2),
                PositionSize, PositionSize);
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return isSupported(instance);
  }

  static boolean isSupported(Object instance) {
    return (instance instanceof ContinuousFunction2D || instance instanceof MapDataUpdater);
  }

  @Override
  protected boolean synchronize(MapDataUpdater current) {
    synchronized (data) {
      if (centerAction.scaleEnabled())
        sampler.resetRange();
      if (!synchronizeAction.synchronizedData())
        return false;
      sampler.updateData(data);
      if (current instanceof PositionFunction2D)
        position = ((PositionFunction2D) current).position();
      return true;
    }
  }

  @Override
  public void onInstanceSet(Clock clock, MapDataUpdater function) {
    super.onInstanceSet(clock, function);
    sampler = function;
    data = sampler.createNewMapData();
    axes.x.reset();
    axes.y.reset();
  }

  @Override
  protected void setToolbar(IToolBarManager toolbarManager) {
    toolbarManager.add(centerAction);
    toolbarManager.add(new ScreenShotAction(this));
    toolbarManager.add(colorMapAction);
    toolbarManager.add(synchronizeAction);
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    colorMapAction.init(memento);
    synchronizeAction.init(memento);
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    colorMapAction.saveState(memento);
    synchronizeAction.saveState(memento);
  }

  @Override
  public void onInstanceUnset(Clock clock) {
    super.onInstanceUnset(clock);
  }

  @Override
  public RequestResult search(final Point mousePosition) {
    if (data == null)
      return null;
    final Point2D.Double position = axes.toD(mousePosition);
    return new RequestResult() {

      @Override
      public String tooltipLabel() {
        return "";
      }

      @Override
      public String fieldLabel() {
        if (data == null)
          return null;
        synchronized (data) {
          // return String.valueOf(sampler.valueOf(data, position));
          return String.valueOf(position.toString());
        }
      }

      @Override
      public boolean dynamicText() {
        return true;
      }

      @Override
      public Point computeMousePosition() {
        return mousePosition;
      }
    };
  }

  @Override
  public boolean emptySearch() {
    return false;
  }
}
