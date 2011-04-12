package zephyr.plugin.filehandling.internal.ui;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import zephyr.plugin.filehandling.internal.FileLoader;

public class OpenFile extends AbstractHandler {
  private static String ExtKey = "defaultExtension";
  private static String FolderKey = "defaultFolder";

  private final IEclipsePreferences instanceScope = new InstanceScope().getNode(FileLoader.PluginID);

  public OpenFile() {
  }

  private String getValue(String key) {
    return instanceScope.get(key, null);
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    FileDialog fd = new FileDialog(HandlerUtil.getActiveShell(event), SWT.OPEN);
    fd.setText("Open");
    String[] extensions = FileLoader.getExtensions();
    String defaultExtension = getValue(ExtKey);
    if (defaultExtension != null)
      reorderExtensions(defaultExtension, extensions);
    // fd.setFilterExtensions(extensions);
    String defaultFolder = getValue(FolderKey);
    if (defaultFolder != null)
      fd.setFilterPath(defaultFolder);
    String selected = fd.open();
    if (selected == null)
      return null;
    registerFileData(selected);
    FileLoader.openFile(selected);
    return null;
  }

  private void registerFileData(String filepath) {
    int extensionIndex = filepath.lastIndexOf('.');
    if (extensionIndex != -1)
      instanceScope.put(ExtKey, filepath.substring(extensionIndex));
    instanceScope.put(FolderKey, new File(filepath).getParent());
  }

  private void reorderExtensions(String defaultExtension, String[] extensions) {
    for (int i = 0; i < extensions.length; i++) {
      if (!extensions[i].endsWith(defaultExtension))
        continue;
      String b = extensions[0];
      extensions[0] = extensions[i];
      extensions[i] = b;
    }
  }
}
