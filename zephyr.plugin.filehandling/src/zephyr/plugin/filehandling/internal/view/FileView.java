package zephyr.plugin.filehandling.internal.view;

import java.util.Arrays;

import org.eclipse.jface.action.IToolBarManager;

import zephyr.ZephyrCore;
import zephyr.plugin.core.actions.RestartAction;
import zephyr.plugin.core.actions.TerminateAction;
import zephyr.plugin.core.api.synchronization.Closeable;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.observations.EnvironmentView;
import zephyr.plugin.core.observations.ObsLayout;
import zephyr.plugin.core.observations.ObsWidget;
import zephyr.plugin.core.observations.SensorGroup;
import zephyr.plugin.core.views.Restartable;
import zephyr.plugin.filehandling.internal.defaulthandler.DefaultHandler;
import zephyr.plugin.filehandling.internal.defaulthandler.LogFile;

public class FileView extends EnvironmentView implements Closeable, Restartable {
  static public class Provider extends ClassViewProvider {
    public Provider() {
      super(LogFile.class, FileView.ID);
    }
  }

  public static final String ID = "zephyr.plugin.filehandling.internal.view.fileview";
  protected LogFile logFile = null;
  double[] currentObservation;
  private final TerminateAction terminateAction;
  private final RestartAction restartAction;

  public FileView() {
    terminateAction = new TerminateAction(this);
    terminateAction.setEnabled(false);
    restartAction = new RestartAction(this);
    restartAction.setEnabled(false);
  }

  @Override
  protected ObsLayout getObservationLayout() {
    String[] labels = logFile.labels();
    int nbLine = (int) Math.sqrt(labels.length) + 1;
    int nbItems = (labels.length / nbLine) + 1;
    ObsWidget[][] widgets = new ObsWidget[nbLine][];
    for (int i = 0; i < nbLine; i++) {
      ObsWidget[] line = new ObsWidget[Math.min(nbItems, labels.length - i * nbItems)];
      for (int j = 0; j < line.length; j++) {
        int labelIndex = i * nbItems + j;
        line[j] = new SensorGroup(labels[labelIndex], Arrays.asList((Integer) labelIndex));
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
  public boolean synchronize() {
    if (logFile == null)
      return false;
    return synchronize(logFile.currentLine());
  }

  @Override
  public void addTimed(Object drawn, Object info) {
    logFile = (LogFile) drawn;
    restartAction.setEnabled(drawn instanceof LogFile);
    terminateAction.setEnabled(true);
    createLayout();
  }

  @Override
  public boolean canTimedAdded() {
    return logFile == null;
  }

  @Override
  public void close() {
    restartAction.setEnabled(false);
    terminateAction.setEnabled(false);
    if (logFile != null) {
      ZephyrCore.removeClock(logFile.clock());
      logFile.close();
      logFile = null;
    }
    super.close();
  }

  @Override
  public void restart() {
    final String filepath = logFile.filepath;
    close();
    ZephyrCore.start(new Runnable() {
      @Override
      public void run() {
        DefaultHandler.handle(filepath);
      }
    });
  }

  @Override
  public void dispose() {
    close();
    super.dispose();
  }
}
