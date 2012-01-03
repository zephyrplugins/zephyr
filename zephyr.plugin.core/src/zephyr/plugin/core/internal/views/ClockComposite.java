package zephyr.plugin.core.internal.views;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.ClockInfo;
import zephyr.plugin.core.helpers.ImageManager;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.clocks.Control;

public class ClockComposite {
  static final double PeriodUpdateRate = 0.99;

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
  private final Group group;
  private final Map<String, UpdatableLabelInfo> captionToLabelInfo = new LinkedHashMap<String, UpdatableLabelInfo>();
  final ImageManager imageManager = new ImageManager();
  private Listener<Control> pauseResumeListener;
  private final Listener<Clock> clockTickListener = new Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      if (averagePeriod == -1)
        averagePeriod = clock.lastPeriodNano();
      averagePeriod = (long) (PeriodUpdateRate * averagePeriod + (1.0 - PeriodUpdateRate) * clock.lastPeriodNano());
    }
  };
  long averagePeriod = -1;

  public ClockComposite(Composite parent, Clock clock) {
    this.clock = clock;
    group = new Group(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    group.setLayout(gridLayout);
    createLabelLine(group);
    timeStepLabel = updatedShortTextLabel(group, "Step");
    periodLabel = updatedShortTextLabel(group, "Period");
    clock.onTick.connect(clockTickListener);
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
    addSuspendButton(buttons);
    addTerminateButton(buttons);
  }

  private void addSuspendButton(Composite buttons) {
    if (!clock.info().isSuspendable)
      return;
    final Control control = ZephyrPluginCore.control();
    final Button button = addButton(buttons, suspendResumeIcon());
    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseUp(MouseEvent e) {
        if (control.isSuspended(clock))
          control.resume(clock);
        else
          control.suspend(clock);
      }
    });
    pauseResumeListener = new Listener<Control>() {
      @Override
      public void listen(Control eventInfo) {
        Display.getDefault().asyncExec(new Runnable() {
          @Override
          public void run() {
            if (!button.isDisposed())
              button.setImage(imageManager.image(ZephyrCore.PluginID, suspendResumeIcon()));
          }
        });
      }
    };
    control.onModeChange.connect(pauseResumeListener);
  }

  protected String suspendResumeIcon() {
    return ZephyrPluginCore.control().isSuspended(clock) ? "icons/action_resume.gif" : "icons/action_suspend.gif";
  }

  private void addTerminateButton(Composite buttons) {
    if (!clock.info().isTerminable)
      return;
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
    button.setImage(imageManager.image(ZephyrCore.PluginID, iconPath));
    return button;
  }

  private Label createTextLabel(Composite parent, String stringLabel, String constantValue, String tooltip) {
    Label valueLabel = updatedShortTextLabel(parent, stringLabel);
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
    ClockInfo clockInfo = clock.info();
    for (String caption : clockInfo.captions()) {
      UpdatableLabelInfo labelInfo = captionToLabelInfo.get(caption);
      if (labelInfo == null) {
        labelInfo = new UpdatableLabelInfo(null, "", "");
        captionToLabelInfo.put(caption, labelInfo);
        continue;
      }
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
    layoutChanged = adjustLabel(periodLabel, Chrono.toPeriodString(averagePeriod)) || layoutChanged;
    HashSet<Map.Entry<String, UpdatableLabelInfo>> labelSet = new LinkedHashSet<Map.Entry<String, UpdatableLabelInfo>>(
                                                                                                                       captionToLabelInfo
                                                                                                                           .entrySet());
    for (Map.Entry<String, UpdatableLabelInfo> entry : labelSet) {
      UpdatableLabelInfo labelInfo = entry.getValue();
      if (labelInfo.label == null) {
        labelInfo.label = createTextLabel(group, entry.getKey(), labelInfo.value, labelInfo.info);
        group.getParent().layout(true);
      } else {
        layoutChanged = adjustLabel(labelInfo.label, labelInfo.value) || layoutChanged;
        labelInfo.label.setToolTipText(labelInfo.info);
      }
    }
    if (layoutChanged)
      group.layout(true);
  }

  public void setLayoutData(GridData griddata) {
    group.setLayoutData(griddata);
  }

  public void dispose() {
    clock.onTick.disconnect(clockTickListener);
    if (pauseResumeListener != null)
      ZephyrPluginCore.control().onModeChange.disconnect(pauseResumeListener);
    group.dispose();
    captionToLabelInfo.clear();
    imageManager.dispose();
  }
}
