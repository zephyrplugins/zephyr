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

public final class SynchronizeAction extends Action {
  private static final String SynchronizedActionKey = "SynchronizedAction";
  private boolean synchronizedData = true;

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
    setChecked(synchronizedData);
  }

  @Override
  public void run() {
    synchronizedData = !synchronizedData;
  }

  public void setImageDescriptor() {
    if (isChecked())
      setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID, "icons/action_graph_connect.png"));
    else
      setImageDescriptor(Helper.getImageDescriptor(ZephyrPlotting.PluginID, "icons/action_graph_disconnect.png"));
  }

  public boolean synchronizedData() {
    return synchronizedData;
  }

  public void init(IMemento memento) {
    Boolean saved = memento != null ? memento.getBoolean(SynchronizedActionKey) : null;
    if (saved != null) {
      synchronizedData = saved;
      setChecked(saved);
      setImageDescriptor();
    }
  }

  public void saveState(IMemento memento) {
    memento.putBoolean(SynchronizedActionKey, isChecked());
  }
}