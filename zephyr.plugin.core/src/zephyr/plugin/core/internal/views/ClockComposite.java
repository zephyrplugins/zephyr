package zephyr.plugin.core.internal.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.ClockInfo;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.utils.Helper;

public class ClockComposite {
  static class UpdatableLabelInfo {
    public Label label;
    public String value;
    public String info;

    public UpdatableLabelInfo(Label label, String value, String info) {
      this.label = label;
      this.value = value;
      this.info = info;
    }
  }

  private final Label timeStepLabel;
  private final Label periodLabel;
  final Clock clock;
  private long timeStep;
  private long period = -1;
  private final Group group;
  private final Map<String, UpdatableLabelInfo> captionToLabelInfo = new HashMap<String, UpdatableLabelInfo>();

  public ClockComposite(Composite parent, Clock clock) {
    this.clock = clock;
    group = new Group(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    group.setLayout(gridLayout);
    ClockInfo clockInfo = clock.info();
    createLabelLine(group);
    timeStepLabel = updatedShortTextLabel(group, "Step");
    periodLabel = updatedShortTextLabel(group, "Period");
    for (String caption : clockInfo.captions())
      updatableTextLabel(group, caption, clockInfo.value(caption), clockInfo.info(caption));
  }

  private void updatableTextLabel(Group group, String caption, String value, String info) {
    Label label = createTextLabel(group, caption, value, info);
    captionToLabelInfo.put(caption, new UpdatableLabelInfo(label, value, info));
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
    addTerminateButton(buttons);
  }

  private void addTerminateButton(Composite buttons) {
    Button button = addButton(buttons, "icons/action_terminate.gif");
    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseUp(MouseEvent e) {
        clock.terminate();
        ZephyrCore.removeClock(clock);
      }
    });
  }

  protected Button addButton(Composite parent, String iconPath) {
    Button button = new Button(parent, SWT.FLAT);
    ImageDescriptor imageDescriptor = Helper.getImageDescriptor(ZephyrPluginCore.PLUGIN_ID, iconPath);
    button.setImage(imageDescriptor.createImage());
    return button;
  }

  private Label createTextLabel(Composite parent, String stringLabel, String constantValue, String tooltip) {
    Label label = new Label(parent, SWT.NONE);
    label.setText(stringLabel);
    Label valueLabel = new Label(parent, SWT.NONE);
    GridData griddata = new GridData(SWT.RIGHT, SWT.UP, true, false);
    valueLabel.setLayoutData(griddata);
    valueLabel.setText(constantValue);
    valueLabel.setToolTipText(tooltip);
    return valueLabel;
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
    ClockInfo clockInfo = clock.info();
    for (String caption : clockInfo.captions()) {
      UpdatableLabelInfo labelInfo = captionToLabelInfo.get(caption);
      labelInfo.value = clockInfo.value(caption);
      labelInfo.info = clockInfo.info(caption);
    }
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
    for (UpdatableLabelInfo labelInfo : captionToLabelInfo.values()) {
      layoutChanged = adjustLabel(labelInfo.label, labelInfo.value) || layoutChanged;
      labelInfo.label.setToolTipText(labelInfo.info);
    }
    if (layoutChanged)
      group.layout(true);
  }

  public void setLayoutData(GridData griddata) {
    group.setLayoutData(griddata);
  }

  public void dispose() {
    group.dispose();
    captionToLabelInfo.clear();
  }
}
