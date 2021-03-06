package zephyr.plugin.plotting.privates.commands;

import org.eclipse.jface.window.Window;

import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.plotting.privates.dialogbox.SelectDataDialog;
import zephyr.plugin.plotting.privates.view.PlotView;

public class SelectTraces extends PlotViewCommandHandler {

  @Override
  protected void execute(PlotView plotView) {
    selectTraces(plotView);
  }

  public static void selectTraces(PlotView view) {
    SelectDataDialog dialog = new SelectDataDialog(view.getViewSite().getShell());
    int dialogResult = dialog.open();
    if (dialogResult != Window.OK)
      return;
    view.plotSelection().setCurrentSelection(dialog.getSelectedTraces());
    ZephyrSync.submitView(view, view.clocks());
  }
}
