package zephyr.plugin.plotting.commands;

import org.eclipse.jface.window.Window;

import zephyr.plugin.plotting.dialogbox.SelectDataDialog;
import zephyr.plugin.plotting.graphs.PlotView;

public class SelectTraces extends PlotViewCommandHandler {

  @Override
  protected void execute(PlotView plotView) {
    SelectDataDialog dialog = new SelectDataDialog(plotView.getViewSite().getShell());
    int dialogResult = dialog.open();
    if (dialogResult != Window.OK)
      return;
    plotView.plotSelection().setCurrentSelection(dialog.getSelectedTraces());
  }
}
