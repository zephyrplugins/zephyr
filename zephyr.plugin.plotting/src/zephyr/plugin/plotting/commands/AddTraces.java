package zephyr.plugin.plotting.commands;


import java.util.Set;

import org.eclipse.jface.window.Window;

import zephyr.plugin.plotting.dialogbox.SelectDataDialog;
import zephyr.plugin.plotting.graphs.PlotView;
import zephyr.plugin.plotting.plots.PlotSelection;
import zephyr.plugin.plotting.traces.Trace;

public class AddTraces extends PlotViewCommandHandler {
  @Override
  protected void execute(PlotView plotView) {
    SelectDataDialog dialog = new SelectDataDialog(plotView.getViewSite().getShell());
    int dialogResult = dialog.open();
    if (dialogResult != Window.OK)
      return;
    PlotSelection plotSelection = plotView.plotSelection();
    Set<Trace> selectedTraces = plotSelection.getCurrentTracesSelection();
    selectedTraces.addAll(dialog.getSelectedTraces());
    plotSelection.setCurrentSelection(selectedTraces);
  }
}
