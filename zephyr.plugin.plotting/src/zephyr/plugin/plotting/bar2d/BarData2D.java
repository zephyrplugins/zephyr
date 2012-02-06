package zephyr.plugin.plotting.bar2d;

import org.eclipse.swt.graphics.Point;

import zephyr.plugin.plotting.axes.Axes;
import zephyr.plugin.plotting.data.Data2D;
import zephyr.plugin.plotting.mousesearch.RequestResult;
import zephyr.plugin.plotting.mousesearch.SearchableData;
import zephyr.plugin.plotting.plot2d.Plot2DRequestResult;

public class BarData2D extends SearchableData<Data2D> {
  private String[] labels = null;

  public BarData2D(Axes axes) {
    super(axes);
  }

  @Override
  protected RequestResult searchData(Point mousePosition) {
    int xPosition = (int) axes.toDX(mousePosition.x);
    String xLabel = null;
    if (labels != null)
      xLabel = xPosition >= 0 && xPosition < labels.length ? labels[xPosition] : "?";
    return new Plot2DRequestResult(axes, searchableData, xPosition, xLabel);
  }

  void updateData(double[] newdata) {
    lockData();
    if (searchableData == null || newdata.length != searchableData.nbPoints) {
      searchableData = new Data2D(newdata.length);
      for (int i = 0; i < searchableData.nbPoints; i++)
        searchableData.xdata[i] = i;
    }
    for (int i = 0; i < newdata.length; i++)
      searchableData.ydata[i] = (float) newdata[i];
    unlockData();
  }

  @Override
  protected boolean isDataEmpty(Data2D data) {
    return data.nbPoints == 0;
  }

  public void reset() {
    lockData();
    searchableData = null;
    unlockData();
  }

  public void setLabels(String[] labels) {
    this.labels = labels;
  }
}
