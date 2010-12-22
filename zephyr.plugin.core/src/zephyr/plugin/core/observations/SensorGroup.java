package zephyr.plugin.core.observations;

import java.util.Arrays;

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
  private final ObsStat[] stats;
  private final Canvas[] canvasArray;
  private final String title;
  private final int[] indexes;
  private final double[] mins;
  private final double[] maxs;

  public SensorGroup(String title, int[] indexes) {
    this(title, indexes, Double.MAX_VALUE, -Double.MAX_VALUE);
  }

  public SensorGroup(String title, int[] indexes, double min, double max) {
    this(title, indexes, toArray(indexes.length, min), toArray(indexes.length, max));
  }

  private static double[] toArray(int length, double value) {
    double[] result = new double[length];
    Arrays.fill(result, value);
    return result;
  }

  public SensorGroup(String title, int[] indexes, double[] mins, double[] maxs) {
    this.title = title;
    this.indexes = indexes.clone();
    stats = new ObsStat[indexes.length];
    canvasArray = new Canvas[indexes.length];
    this.mins = mins.clone();
    this.maxs = maxs.clone();
  }

  private void layoutRichBar(Group group) {
    FillLayout fillLayout = new FillLayout();
    fillLayout.type = SWT.HORIZONTAL;
    group.setLayout(fillLayout);
    group.setToolTipText(title);
    for (int i = 0; i < indexes.length; i++) {
      Canvas canvas = new Canvas(group, SWT.DOUBLE_BUFFERED);
      ObsStat obsState = new ObsStat(title + " " + i, indexes[i], mins[i], maxs[i]);
      final RichDisplayBar displayBar = new RichDisplayBar(obsState);
      canvas.addPaintListener(new PaintListener() {
        @Override
        public void paintControl(PaintEvent event) {
          displayBar.paint(event.gc);
        }
      });
      canvasArray[i] = canvas;
      stats[i] = obsState;
    }
  }

  @Override
  synchronized public void createWidgetComposite(Composite parent) {
    if (indexes.length == 0)
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
    for (Canvas canvas : canvasArray)
      canvas.redraw();
  }

  @Override
  public boolean hasContent() {
    return indexes.length > 0;
  }
}
