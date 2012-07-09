package zephyr.plugin.core.privates.views.clocks;

import java.util.LinkedHashMap;
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
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.ClockInfo;
import zephyr.plugin.core.internal.helpers.ImageManager;
import zephyr.plugin.core.privates.ZephyrPluginCore;
import zephyr.plugin.core.privates.clocks.ClockStat;
import zephyr.plugin.core.privates.clocks.Control;

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
  private final Label modelPeriodLabel;
  private final Label fullPeriodLabel;
  final Clock clock;
  private long timeStep;
  private final Group group;
  private final Map<String, UpdatableLabelInfo> captionToLabelInfo = new LinkedHashMap<String, UpdatableLabelInfo>();
  final ImageManager imageManager = new ImageManager();
  private Listener<Control> pauseResumeListener;
  private final ClockStat clockStats;

  public ClockComposite(Composite parent, Clock clock) {
    this.clock = clock;
    group = new Group(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    group.setLayout(gridLayout);
    createLabelLine(group);
    timeStepLabel = updatedShortTextLabel(group, "Step");
    fullPeriodLabel = updatedShortTextLabel(group, "Period");
    modelPeriodLabel = updatedShortTextLabel(group, "Model Period");
    clockStats = ZephyrPluginCore.clocks().clockStats(clock);
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
        clock.prepareTermination();
        ZephyrCore.removeClock(clock);
      }
    });
  }

  protected Button addButton(Composite parent, String iconPath) {
    Button button = new Button(parent, SWT.FLAT);
    button.setImage(imageManager.image(ZephyrCore.PluginID, iconPath));
    return button;
  }

  private static Label createTextLabel(Composite parent, String stringLabel, String constantValue, String tooltip) {
    Label valueLabel = updatedShortTextLabel(parent, stringLabel);
    valueLabel.setText(constantValue);
    valueLabel.setToolTipText(tooltip);
    return valueLabel;
  }

  private static Label updatedShortTextLabel(Composite parent, String stringLabel) {
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

  private static boolean adjustLabel(Label label, String text) {
    boolean labelLengthChanged = label.getText().length() != text.length();
    label.setText(text);
    return labelLengthChanged;
  }

  public void repaint() {
    boolean layoutChanged = false;
    layoutChanged = adjustLabel(timeStepLabel, String.valueOf(timeStep)) || layoutChanged;
    layoutChanged = adjustLabel(modelPeriodLabel, Chrono.toPeriodString(clockStats.modelPeriod())) || layoutChanged;
    layoutChanged = adjustLabel(fullPeriodLabel, Chrono.toPeriodString(clockStats.fullPeriod())) || layoutChanged;
    Map<String, UpdatableLabelInfo> labelMap = new LinkedHashMap<String, ClockComposite.UpdatableLabelInfo>(
                                                                                                            captionToLabelInfo);
    for (Map.Entry<String, UpdatableLabelInfo> entry : labelMap.entrySet()) {
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
    if (pauseResumeListener != null)
      ZephyrPluginCore.control().onModeChange.disconnect(pauseResumeListener);
    group.dispose();
    captionToLabelInfo.clear();
    imageManager.dispose();
  }
}
