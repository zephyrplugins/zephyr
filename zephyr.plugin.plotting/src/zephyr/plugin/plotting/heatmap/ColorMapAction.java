package zephyr.plugin.plotting.heatmap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IMemento;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.utils.Helper;

public class ColorMapAction extends Action {
  private static final String ColorMapActionKey = "ColorMapAction";
  private final Function2DDrawer drawer;

  public ColorMapAction(Function2DDrawer drawer) {
    super("Color Map", IAction.AS_CHECK_BOX);
    this.drawer = drawer;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID, "icons/action_colormap.png"));
  }

  @Override
  public void run() {
    drawer.setColorMap(isChecked() ? Function2DDrawer.BWColorMap : Function2DDrawer.PinkColorMap);
  }

  public void init(IMemento memento) {
    Boolean saved = memento.getBoolean(ColorMapActionKey);
    if (saved != null) {
      setChecked(saved);
      run();
    }
  }

  public void saveState(IMemento memento) {
    memento.putBoolean(ColorMapActionKey, isChecked());
  }
}
