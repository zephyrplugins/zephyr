package zephyr.plugin.core.internal.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.ClockInfo;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.utils.Helper;

public class ClockComposite {
  private final Label timeStepLabel;
  private final Label periodLabel;
  private final Clock clock;
  private long timeStep;
  private long period = -1;
  private final Group group;

  public ClockComposite(Composite parent, Clock clock) {
    this.clock = clock;
    group = new Group(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    group.setLayout(gridLayout);
    ClockInfo clockInfo = clock.info();
    createLabelLine(group);
    for (String caption : clockInfo.captions()) {
      constantTextLabel(group, caption, clockInfo.value(caption), clockInfo.info(caption));
    }
    timeStepLabel = updatedShortTextLabel(group, "Step");
    periodLabel = updatedShortTextLabel(group, "Period");
  }

  private void createLabelLine(Composite parent) {
    Label label = new Label(parent, SWT.NONE);
    label.setText(clock.info().label());
    Composite buttons = new Composite(parent, SWT.NONE);
    GridData griddata = new GridData(SWT.RIGHT, SWT.UP, true, false);
    buttons.setLayoutData(griddata);
    RowLayout buttonsLayout = new RowLayout();
    buttons.setLayout(buttonsLayout);
    buttonsLayout.marginTop = 0;
    buttonsLayout.marginBottom = 0;
    // addButton(buttons, "icons/action_suspend.gif");
    // addButton(buttons, "icons/action_restart.gif");
    addButton(buttons, "icons/action_terminate.gif");
  }

  protected void addButton(Composite parent, String iconPath) {
    Button terminateButton = new Button(parent, SWT.FLAT);
    ImageDescriptor imageDescriptor = Helper.getImageDescriptor(ZephyrPluginCore.PLUGIN_ID, iconPath);
    terminateButton.setImage(imageDescriptor.createImage());
  }

  private void constantTextLabel(Composite parent, String stringLabel, String constantValue, String tooltip) {
    Label label = new Label(parent, SWT.NONE);
    label.setText(stringLabel);
    Label valueLabel = new Label(parent, SWT.NONE);
    GridData griddata = new GridData(SWT.RIGHT, SWT.UP, true, false);
    valueLabel.setLayoutData(griddata);
    valueLabel.setText(constantValue);
    valueLabel.setToolTipText(tooltip);
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
    period = clock.lastPeriodNano();
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

  public void dispose() {
    group.dispose();
  }
}
