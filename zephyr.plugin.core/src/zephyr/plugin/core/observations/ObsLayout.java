package zephyr.plugin.core.observations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObsLayout implements Iterable<ObsWidget> {
  private final ObsWidget[][] widgetArray;

  public ObsLayout(ObsWidget[][] widgetArray) {
    this.widgetArray = widgetArray.clone();
  }

  public ObsWidget[][] widgetArray() {
    return widgetArray.clone();
  }

  @Override
  public Iterator<ObsWidget> iterator() {
    List<ObsWidget> widgets = new ArrayList<ObsWidget>();
    for (ObsWidget[] widgetLine : widgetArray)
      for (ObsWidget widget : widgetLine)
        widgets.add(widget);
    return widgets.iterator();
  }
}
