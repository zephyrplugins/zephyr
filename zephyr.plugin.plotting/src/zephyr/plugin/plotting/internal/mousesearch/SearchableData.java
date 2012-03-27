package zephyr.plugin.plotting.internal.mousesearch;

import java.util.concurrent.Semaphore;

import org.eclipse.swt.graphics.Point;

import zephyr.plugin.plotting.internal.axes.Axes;

public abstract class SearchableData<T> implements MouseSearchable {
  public T searchableData = null;
  protected final Axes axes;
  private final Semaphore dataLock = new Semaphore(1);

  public SearchableData(Axes axes) {
    this(axes, null);
  }

  public SearchableData(Axes axes, T defaultValue) {
    this.axes = axes;
    this.searchableData = defaultValue;
  }

  @Override
  public RequestResult search(Point mousePosition) {
    if (searchableData == null || emptySearch())
      return null;
    lockData();
    RequestResult result = searchData(mousePosition);
    unlockData();
    return result;
  }

  public void lockData() {
    try {
      dataLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void unlockData() {
    dataLock.release();
  }

  abstract protected RequestResult searchData(Point mousePosition);

  @Override
  synchronized public boolean emptySearch() {
    if (searchableData == null)
      return true;
    lockData();
    boolean result = isDataEmpty(searchableData);
    unlockData();
    return result;
  }

  abstract protected boolean isDataEmpty(T data);
}
