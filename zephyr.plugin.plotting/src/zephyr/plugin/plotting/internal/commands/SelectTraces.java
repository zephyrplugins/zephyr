package zephyr.plugin.plotting.internal.commands;

import org.eclipse.jface.window.Window;

import zephyr.ZephyrSync;
import zephyr.plugin.plotting.internal.dialogbox.SelectDataDialog;
import zephyr.plugin.plotting.internal.graphs.PlotView;

public class SelectTraces extends PlotViewCommandHandler {

  @Override
  protected void execute(PlotView plotView) {
    SelectDataDialog dialog = new SelectDataDialog(plotView.getViewSite().getShell());
    int dialogResult = dialog.open();
    if (dialogResult != Window.OK)
      return;
    plotView.plotSelection().setCurrentSelection(dialog.getSelectedTraces());
    ZephyrSync.submitView(plotView);
  }
}
