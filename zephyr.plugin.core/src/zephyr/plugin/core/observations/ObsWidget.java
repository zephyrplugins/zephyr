package zephyr.plugin.core.observations;

import org.eclipse.swt.widgets.Composite;

public interface ObsWidget {
  Composite createWidgetComposite(Composite parent);

  void updateValue(double[] currentObservation);

  void repaint();
}
