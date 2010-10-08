package zephyr.plugin.core.observations;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class SensorCollection implements ObsWidget {

  private final String collectionLabel;
  private final ObsWidget[] widgets;

  public SensorCollection(String collectionLabel, ObsWidget... obsWidget) {
    this.collectionLabel = collectionLabel;
    widgets = obsWidget;
  }

  @Override
  synchronized public void createWidgetComposite(Composite parent) {
    if (!hasContent())
      return;
    Group group = new Group(parent, SWT.NONE);
    group.setText(collectionLabel);
    FillLayout fillLayout = new FillLayout();
    fillLayout.type = SWT.HORIZONTAL;
    group.setLayout(fillLayout);
    for (ObsWidget widget : widgets)
      widget.createWidgetComposite(group);
  }

  @Override
  synchronized public void updateValue(double[] currentObservation) {
    for (ObsWidget widget : widgets)
      widget.updateValue(currentObservation);
  }

  @Override
  public void repaint() {
    for (ObsWidget widget : widgets)
      widget.repaint();
  }

  @Override
  public boolean hasContent() {
    return EnvironmentView.hasContent(widgets);
  }
}
