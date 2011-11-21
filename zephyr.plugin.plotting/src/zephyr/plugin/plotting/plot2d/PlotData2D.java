package zephyr.plugin.plotting.plot2d;

import java.awt.geom.Point2D;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.swt.graphics.Point;

import zephyr.plugin.plotting.axes.Axes;
import zephyr.plugin.plotting.data.Data2D;
import zephyr.plugin.plotting.mousesearch.RequestResult;
import zephyr.plugin.plotting.mousesearch.SearchableData;

public class PlotData2D extends SearchableData<Set<Data2D>> {
  public PlotData2D(Axes axes) {
    super(axes, new LinkedHashSet<Data2D>());
  }

  @Override
  protected RequestResult searchData(Point mousePosition) {
    Point2D.Double dataPoint = axes.toD(mousePosition);
    Data2D bestData = null;
    int xIndex = -1;
    double bestDistance = Double.MAX_VALUE;
    for (Data2D data : searchableData)
      for (int i = 0; i < data.nbPoints; i++) {
        double distance = dataPoint.distance(data.xdata[i], data.ydata[i]);
        if (distance < bestDistance) {
          bestDistance = distance;
          xIndex = i;
          bestData = data;
        }
      }
    if (bestData == null)
      return null;
    return new Plot2DRequestResult(axes, bestData, xIndex);
  }

  @Override
  protected boolean isDataEmpty(Set<Data2D> data) {
    return searchableData.isEmpty();
  }

  public void reset() {
    lockData();
    searchableData.clear();
    unlockData();
  }

  public void add(Data2D data) {
    lockData();
    searchableData.add(data);
    unlockData();
  }
}
