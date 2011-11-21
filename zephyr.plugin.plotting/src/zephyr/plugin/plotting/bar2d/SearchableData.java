package zephyr.plugin.plotting.bar2d;

import org.eclipse.swt.graphics.Point;

import zephyr.plugin.plotting.axes.Axes;
import zephyr.plugin.plotting.data.Data2D;
import zephyr.plugin.plotting.mousesearch.MouseSearchable;
import zephyr.plugin.plotting.mousesearch.RequestResult;
import zephyr.plugin.plotting.plot2d.Plot2DRequestResult;

public class SearchableData implements MouseSearchable {
  private volatile Data2D data = null;
  private final Axes axes;

  public SearchableData(Axes axes) {
    this.axes = axes;
  }

  @Override
  synchronized public RequestResult search(Point mousePosition) {
    if (data == null)
      return null;
    return new Plot2DRequestResult(axes, data, (int) axes.toDX(mousePosition.x));
  }

  synchronized void updateData(double[] newdata) {
    if (data == null || newdata.length != data.nbPoints) {
      data = new Data2D(newdata.length);
      for (int i = 0; i < data.nbPoints; i++)
        data.xdata[i] = i;
    }
    for (int i = 0; i < newdata.length; i++)
      data.ydata[i] = (float) newdata[i];
  }

  @Override
  synchronized public boolean emptySearch() {
    return data == null;
  }

  synchronized public void reset() {
    data = null;
  }
}
