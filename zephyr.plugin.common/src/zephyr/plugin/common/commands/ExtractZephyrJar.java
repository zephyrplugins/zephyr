package zephyr.plugin.common.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ExtractZephyrJar extends AbstractHandler {
  private final File jarFile;

  public ExtractZephyrJar() {
    jarFile = extractJarFile();
  }

  private boolean isJarFileValid() {
    return jarFile.canRead() && jarFile.isFile();
  }

  private File extractJarFile() {
    File path = null;
    try {
      path = FileLocator.getBundleFile(Platform.getBundle("rlpark.plugin.utils"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return path;
  }

  public static void copyFile(File sourceFile, File destFile) throws IOException {
    if (!destFile.exists())
      destFile.createNewFile();

    FileChannel source = null;
    FileChannel destination = null;
    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    } finally {
      if (source != null)
        source.close();
      if (destination != null)
        destination.close();
    }
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    if (!isJarFileValid()) {
      MessageDialog.openError(shell, "Extract Zephyr Jar",
                              "Command not valid when Zephyr is started from Eclipse");
      return null;
    }
    FileDialog fd = new FileDialog(shell, SWT.SAVE);
    fd.setText("Save Zephyr Jar");
    fd.setFilterExtensions(new String[] { "*.jar" });
    fd.setFileName("zephyr.jar");
    String selected = fd.open();
    if (selected == null)
      return null;
    try {
      copyFile(jarFile, new File(selected));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
