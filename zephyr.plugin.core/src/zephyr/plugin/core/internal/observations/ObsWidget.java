package zephyr.plugin.core.internal.observations;

import org.eclipse.swt.widgets.Composite;

public interface ObsWidget {
  void createWidgetComposite(Composite parent);

  void updateValue(double[] currentObservation);

  void repaint();

  boolean hasContent();
}
