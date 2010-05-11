package zephyr.plugin.plotting.commands;

import zephyr.plugin.plotting.graphs.PlotView;

public class CenterPlot extends PlotViewCommandHandler {
  @Override
  protected void execute(PlotView plotView) {
    plotView.showDrawingProgress();
    plotView.plotOverTime().resetAxes(true);
  }
}
