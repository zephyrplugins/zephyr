/**
 * 
 */
package zephyr.plugin.plotting.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IMemento;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.internal.utils.Helper;

public final class DisplayGridAction extends Action {
  private static final String GridActionKey = "GridAction";

  public DisplayGridAction() {
    super("Draw grid", IAction.AS_CHECK_BOX);
    addPropertyChangeListener(new IPropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals("checked"))
          setImageDescriptor();
      }
    });
    setImageDescriptor();
    setId("zephyr.plugin.plotting.action.toggledrawgrid");
    setChecked(false);
  }

  @Override
  public void run() {
  }

  public void setImageDescriptor() {
    setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID, "icons/action_grid.png"));
  }

  public boolean drawGrid() {
    return isChecked();
  }

  public void init(IMemento memento) {
    Boolean saved = memento != null ? memento.getBoolean(GridActionKey) : null;
    if (saved != null) {
      setChecked(saved);
      setImageDescriptor();
    }
  }

  public void saveState(IMemento memento) {
    memento.putBoolean(GridActionKey, isChecked());
  }
}