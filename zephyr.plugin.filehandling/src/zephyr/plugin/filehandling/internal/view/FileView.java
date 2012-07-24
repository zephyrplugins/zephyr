package zephyr.plugin.filehandling.internal.view;

import java.io.File;
import org.eclipse.jface.action.IToolBarManager;
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.logfiles.LogFile;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.actions.RestartAction;
import zephyr.plugin.core.internal.actions.TerminateAction;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.observations.EnvironmentView;
import zephyr.plugin.core.internal.observations.ObsLayout;
import zephyr.plugin.core.internal.observations.ObsWidget;
import zephyr.plugin.core.internal.observations.SensorGroup;
import zephyr.plugin.core.internal.views.Restartable;
import zephyr.plugin.filehandling.internal.DefaultHandler;

public class FileView extends EnvironmentView<LogFile> implements Restartable {
  static public class Provider extends ClassViewProvider {
    public Provider() {
      super(LogFile.class);
    }
  }

  String filepath;
  private final TerminateAction terminateAction;
  private final RestartAction restartAction;

  public FileView() {
    terminateAction = new TerminateAction(this);
    terminateAction.setEnabled(false);
    restartAction = new RestartAction(this);
    restartAction.setEnabled(false);
  }

  @Override
  protected ObsLayout getObservationLayout(Clock clock, LogFile current) {
    String[] labels = current.labels();
    int nbLine = (int) Math.sqrt(labels.length);
    int nbItems = labels.length / nbLine;
    ObsWidget[][] widgets = new ObsWidget[nbLine][];
    for (int i = 0; i < nbLine; i++) {
      ObsWidget[] line = new ObsWidget[Math.min(nbItems, labels.length - i * nbItems)];
      for (int j = 0; j < line.length; j++) {
        int labelIndex = i * nbItems + j;
        line[j] = new SensorGroup(labels[labelIndex], new int[] { labelIndex });
      }
      widgets[i] = line;
    }
    return new ObsLayout(widgets);
  }

  @Override
  protected void setToolbar(IToolBarManager toolBarManager) {
    toolBarManager.add(restartAction);
    toolBarManager.add(terminateAction);
  }

  @Override
  public void restart() {
    close();
    ZephyrCore.start(new Runnable() {
      @Override
      public void run() {
        DefaultHandler.handle(filepath);
      }
    });
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return LogFile.class.isInstance(instance);
  }

  @Override
  protected void setLayout(Clock clock, LogFile current) {
    super.setLayout(clock, current);
    restartAction.setEnabled(true);
    terminateAction.setEnabled(true);
    filepath = current.filepath;
  }

  @Override
  protected void setViewName(CodeNode codeNode, LogFile instance) {
    setViewName(new File(instance.filepath).getName(), instance.filepath);
  }


  @Override
  protected boolean synchronize(LogFile current) {
    double[] currentLine = current.currentLine();
    synchronize(currentLine);
    return currentLine != null;
  }

  @Override
  protected void unsetLayout() {
    super.unsetLayout();
    restartAction.setEnabled(false);
    terminateAction.setEnabled(false);
  }
}
