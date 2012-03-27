package zephyr.plugin.plotting.internal.heatmap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IMemento;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.utils.Helper;
import zephyr.plugin.core.internal.views.SyncView;

public class ColorMapAction extends Action {
  private static final String ColorMapActionKey = "ColorMapAction";
  private final Function2DDrawer drawer;
  private final SyncView view;

  public ColorMapAction(SyncView view, Function2DDrawer drawer) {
    super("Color Map", IAction.AS_CHECK_BOX);
    this.view = view;
    this.drawer = drawer;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID, "icons/action_colormap.png"));
  }

  @Override
  public void run() {
    drawer.setColorMap(isChecked() ? Function2DDrawer.BWColorMap : Function2DDrawer.DarkBlueColorMap);
    ZephyrSync.submitView(view);
  }

  public void init(IMemento memento) {
    Boolean saved = memento != null ? memento.getBoolean(ColorMapActionKey) : null;
    if (saved != null) {
      setChecked(saved);
      run();
    }
  }

  public void saveState(IMemento memento) {
    memento.putBoolean(ColorMapActionKey, isChecked());
  }
}
