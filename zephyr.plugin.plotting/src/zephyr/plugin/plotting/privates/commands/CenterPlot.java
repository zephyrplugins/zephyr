package zephyr.plugin.plotting.privates.commands;

import zephyr.plugin.plotting.privates.view.PlotView;

public class CenterPlot extends PlotViewCommandHandler {
  @Override
  protected void execute(PlotView plotView) {
    plotView.center();
  }
}
