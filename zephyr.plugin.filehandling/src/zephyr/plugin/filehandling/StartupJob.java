package zephyr.plugin.filehandling;

import java.io.File;
import java.util.List;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import zephyr.plugin.common.ZephyrPluginCommon;

public class StartupJob implements zephyr.plugin.common.startup.StartupJob {
  @Override
  public int level() {
    return 100;
  }

  @Override
  public void run() {
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        registerDnDTarget();
      }
    });
    parseCommandLine();
  }

  protected void registerDnDTarget() {
    for (Shell shell : PlatformUI.getWorkbench().getDisplay().getShells()) {
      DropTarget dt = new DropTarget(shell, DND.DROP_DEFAULT | DND.DROP_MOVE);
      dt.setTransfer(new Transfer[] { FileTransfer.getInstance() });
      dt.addDropListener(new DropTargetAdapter() {
        @Override
        public void drop(DropTargetEvent event) {
          String fileList[] = null;
          FileTransfer ft = FileTransfer.getInstance();
          if (ft.isSupportedType(event.currentDataType)) {
            fileList = (String[]) event.data;
          }
          for (String filepath : fileList)
            openFileCatched(filepath);
        }
      });
    }
  }

  protected void openFileCatched(String filepath) {
    ZephyrPluginFileHandling.fileLoader().openFile(filepath);
  }

  protected void parseCommandLine() {
    List<String> args = ZephyrPluginCommon.getArgsFiltered();
    for (String arg : args)
      if (new File(arg).canRead())
        openFileCatched(arg);
  }
}
