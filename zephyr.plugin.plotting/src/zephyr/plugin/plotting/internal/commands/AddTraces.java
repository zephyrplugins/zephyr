package zephyr.plugin.plotting.internal.commands;


import java.util.Set;

import org.eclipse.jface.window.Window;

import zephyr.ZephyrSync;
import zephyr.plugin.plotting.internal.dialogbox.SelectDataDialog;
import zephyr.plugin.plotting.internal.plots.PlotSelection;
import zephyr.plugin.plotting.internal.traces.Trace;
import zephyr.plugin.plotting.internal.view.PlotView;

public class AddTraces extends PlotViewCommandHandler {
  @Override
  protected void execute(PlotView plotView) {
    addTraces(plotView);
  }

  static public void addTraces(PlotView plotView) {
    SelectDataDialog dialog = new SelectDataDialog(plotView.getViewSite().getShell());
    int dialogResult = dialog.open();
    if (dialogResult != Window.OK)
      return;
    PlotSelection plotSelection = plotView.plotSelection();
    Set<Trace> selectedTraces = plotSelection.getCurrentTracesSelection();
    selectedTraces.addAll(dialog.getSelectedTraces());
    plotSelection.setCurrentSelection(selectedTraces);
    ZephyrSync.submitView(plotView);
  }
}
