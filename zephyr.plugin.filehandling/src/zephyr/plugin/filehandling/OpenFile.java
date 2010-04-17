package zephyr.plugin.filehandling;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenFile extends AbstractHandler {

  public OpenFile() {
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    FileDialog fd = new FileDialog(HandlerUtil.getActiveShell(event), SWT.OPEN);
    fd.setText("Open");
    FileLoader fileLoader = ZephyrPluginFileHandling.fileLoader();
    fd.setFilterExtensions(fileLoader.getExtensions());
    String selected = fd.open();
    if (selected == null)
      return null;
    fileLoader.openFile(selected);
    return null;
  }
}
