/**
 * 
 */
package zephyr.plugin.plotting.internal.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.utils.Helper;

final class SynchronizeAction extends Action {
  private boolean synchronizedData;

  public SynchronizeAction() {
    super("Synchronize", IAction.AS_CHECK_BOX);
    addPropertyChangeListener(new IPropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals("checked"))
          setImageDescriptor();
      }
    });
    setImageDescriptor();
    setId("zephyr.plugin.plotting.action.togglesynchronized");
  }

  @Override
  public void run() {
    synchronizedData = !synchronizedData;
  }

  public void setImageDescriptor() {
    if (isChecked())
      setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID,
                                                   "icons/action_graph_connect.png"));
    else
      setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID,
                                                   "icons/action_graph_disconnect.png"));
  }

  public boolean synchronizedData() {
    return synchronizedData;
  }
}