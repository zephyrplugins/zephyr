package zephyr.plugin.plotting.bar2d;

import org.eclipse.swt.graphics.Point;

import zephyr.plugin.plotting.axes.Axes;
import zephyr.plugin.plotting.data.Data2D;
import zephyr.plugin.plotting.mousesearch.RequestResult;
import zephyr.plugin.plotting.mousesearch.SearchableData;
import zephyr.plugin.plotting.plot2d.Plot2DRequestResult;

public class BarData2D extends SearchableData<Data2D> {
  public BarData2D(Axes axes) {
    super(axes);
  }

  @Override
  protected RequestResult searchData(Point mousePosition) {
    return new Plot2DRequestResult(axes, searchableData, (int) axes.toDX(mousePosition.x));
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
}
