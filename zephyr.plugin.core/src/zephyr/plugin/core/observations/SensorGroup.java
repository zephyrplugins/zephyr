package zephyr.plugin.core.observations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import zephyr.plugin.core.internal.observations.ObsStat;
import zephyr.plugin.core.internal.observations.RichDisplayBar;

public class SensorGroup implements ObsWidget {
  private final List<ObsStat> stats = new ArrayList<ObsStat>();
  private final List<Canvas> canvasList = new ArrayList<Canvas>();
  private final String title;
  private final List<Integer> indexes;
  private final double min;
  private final double max;

  public SensorGroup(String title, List<Integer> indexes) {
    this(title, indexes, Double.MAX_VALUE, -Double.MAX_VALUE);
  }

  public SensorGroup(String title, List<Integer> indexes, double min, double max) {
    this.title = title;
    this.indexes = indexes;
    this.min = min;
    this.max = max;
  }

  private void layoutRichBar(Group group) {
    FillLayout fillLayout = new FillLayout();
    fillLayout.type = SWT.HORIZONTAL;
    group.setLayout(fillLayout);
    group.setToolTipText(title);
    for (int i = 0; i < indexes.size(); i++) {
      Canvas canvas = new Canvas(group, SWT.DOUBLE_BUFFERED);
      ObsStat obsState = new ObsStat(title + " " + i, indexes.get(i), min, max);
      final RichDisplayBar displayBar = new RichDisplayBar(obsState);
      canvas.addPaintListener(new PaintListener() {
        @Override
        public void paintControl(PaintEvent event) {
          displayBar.paint(event.gc);
        }
      });
      canvasList.add(canvas);
      stats.add(obsState);
    }
  }


  @Override
  synchronized public void createWidgetComposite(Composite parent) {
    if (indexes.size() == 0)
      return;
    Group group = new Group(parent, SWT.NONE);
    group.setText(title);
    FillLayout fillLayout = new FillLayout();
    fillLayout.type = SWT.HORIZONTAL;
    group.setLayout(fillLayout);
    layoutRichBar(group);
  }

  @Override
  synchronized public void updateValue(double[] currentObservation) {
    for (ObsStat stat : stats)
      stat.updateValue(currentObservation);
  }

  @Override
  public void repaint() {
    for (Canvas canvas : canvasList)
      canvas.redraw();
  }

  @Override
  public boolean hasContent() {
    return indexes.size() > 0;
  }
}
