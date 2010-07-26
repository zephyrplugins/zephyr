/**
 * 
 */
package zephyr.plugin.plotting.internal.graphs;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import zephyr.plugin.core.utils.Helper;
import zephyr.plugin.plotting.ZephyrPluginPlotting;

final class SynchronizeAction extends Action {
  private final PlotView plotView;

  public SynchronizeAction(PlotView plotView) {
    super("Synchronize", IAction.AS_CHECK_BOX);
    this.plotView = plotView;
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
    plotView.synchronizeData = isChecked();
  }

  public void setImageDescriptor() {
    if (isChecked())
      setImageDescriptor(Helper.getImageDescriptor(ZephyrPluginPlotting.PLUGIN_ID,
                                                   "icons/action_graph_connect.png"));
    else
      setImageDescriptor(Helper.getImageDescriptor(ZephyrPluginPlotting.PLUGIN_ID,
                                                   "icons/action_graph_disconnect.png"));
  }
}