package zephyr.plugin.filehandling;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import zephyr.plugin.core.ZephyrPluginCommon;

public class StartupJob implements zephyr.plugin.core.startup.StartupJob {
  @Override
  public int level() {
    return 20;
  }

  @Override
  public void run() {
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        registerDragAndDropTarget();
      }
    });
    parseCommandLine();
  }

  protected void registerDragAndDropTarget() {
    for (Shell shell : PlatformUI.getWorkbench().getDisplay().getShells()) {
      DropTarget dt = new DropTarget(shell, DND.DROP_DEFAULT | DND.DROP_MOVE);
      dt.setTransfer(new Transfer[] { FileTransfer.getInstance() });
      dt.addDropListener(new DropTargetAdapter() {
        @Override
        public void drop(DropTargetEvent event) {
          String fileList[] = null;
          FileTransfer ft = FileTransfer.getInstance();
          if (ft.isSupportedType(event.currentDataType))
            fileList = (String[]) event.data;
          for (String filepath : fileList)
            ZephyrPluginFileHandling.fileLoader().openFile(filepath);
        }
      });
    }
  }

  protected void parseCommandLine() {
    List<String> args = ZephyrPluginCommon.getArgsFiltered();
    for (String zephyrArg : args) {
      String[] splited = zephyrArg.split(":");
      final String filepath = splited[0];
      if (new File(filepath).canRead()) {
        String[] fileArgs = Arrays.copyOfRange(splited, 1, splited.length);
        ZephyrPluginFileHandling.fileLoader().openFile(filepath, fileArgs);
      }
    }
  }
}
