package zephyr.plugin.jarhandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.jarhandler.JarFileHandler.JarRunnable;

public class JarComposite {
  static final double decay = 0.9;
  private final Label timeStepLabel;
  private final Label periodLabel;
  private final Clock clock;
  private long timeStep;
  private long period = -1;
  private final Group group;

  public JarComposite(Composite parent, JarRunnable jar) {
    clock = jar.clock;
    group = new Group(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    group.setLayout(gridLayout);
    constantTextLabel(group, "Filename: ", jar.filename());
    constantTextLabel(group, "Class: ", jar.runnable.getClass().getCanonicalName());
    timeStepLabel = updatedShortTextLabel(group, "Step: ");
    periodLabel = updatedShortTextLabel(group, "Period: ");
  }

  private void constantTextLabel(Composite parent, String stringLabel, String constantValue) {
    Label label = new Label(parent, SWT.NONE);
    label.setText(stringLabel);
    Label valueLabel = new Label(parent, SWT.NONE);
    GridData griddata = new GridData(SWT.RIGHT, SWT.UP, true, false);
    valueLabel.setLayoutData(griddata);
    valueLabel.setText(constantValue);
  }

  private Label updatedShortTextLabel(Composite parent, String stringLabel) {
    Label label = new Label(parent, SWT.NONE);
    label.setText(stringLabel);
    Label valueLabel = new Label(parent, SWT.NONE);
    valueLabel.setText("-");
    GridData griddata = new GridData(SWT.RIGHT, SWT.UP, true, false);
    valueLabel.setLayoutData(griddata);
    return valueLabel;
  }

  public void synchronize() {
    timeStep = clock.timeStep();
    period = (long) (period == -1 ? clock.lastPeriodNano() : period * decay + clock.lastPeriodNano() * (1 - decay));
  }

  private boolean adjustLabel(Label label, String text) {
    boolean labelLengthChanged = label.getText().length() != text.length();
    label.setText(text);
    return labelLengthChanged;
  }

  public void repaint() {
    boolean layoutChanged = false;
    layoutChanged = adjustLabel(timeStepLabel, String.valueOf(timeStep)) || layoutChanged;
    layoutChanged = adjustLabel(periodLabel, Chrono.toPeriodString(period)) || layoutChanged;
    if (layoutChanged)
      group.layout(true);
  }

  public void setLayoutData(GridData griddata) {
    group.setLayoutData(griddata);
  }
}
