package zephyr.plugin.plotting.internal.commands;

import zephyr.plugin.plotting.internal.graphs.PlotView;

public class CenterPlot extends PlotViewCommandHandler {
  @Override
  protected void execute(PlotView plotView) {
    plotView.center();
  }
}
