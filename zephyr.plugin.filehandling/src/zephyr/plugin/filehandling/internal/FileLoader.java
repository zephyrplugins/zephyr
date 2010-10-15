package zephyr.plugin.filehandling.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import zephyr.ZephyrCore;
import zephyr.plugin.filehandling.IFileHandler;
import zephyr.plugin.filehandling.internal.defaulthandler.DefaultHandler;


public class FileLoader {
  public static String PluginID = "zephyr.plugin.filehandling";
  private static List<IFileHandler> fileHandlers = null;
  private static IFileHandler defaultHandler = new DefaultHandler();

  private FileLoader() {
  }

  static private void loadFileHandlersIFN() {
    if (fileHandlers != null)
      return;
    IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(IFileHandler.ID);
    fileHandlers = new ArrayList<IFileHandler>();
    for (IConfigurationElement configurationElement : config) {
      Object o = null;
      try {
        o = configurationElement.createExecutableExtension("class");
      } catch (CoreException e) {
        e.printStackTrace();
        continue;
      }
      if (o instanceof IFileHandler)
        fileHandlers.add((IFileHandler) o);
    }
  }

  static public void openFile(String filepath) {
    openFile(filepath, new String[0]);
  }

  static public void openFile(String filepath, String[] fileargs) {
    loadFileHandlersIFN();
    if (!new File(filepath).canRead()) {
      displayFileNotFoundMessage(filepath);
      return;
    }
    for (IFileHandler fileHandler : fileHandlers)
      for (String extension : fileHandler.extensions())
        if (filepath.endsWith(extension)) {
          handleFile(fileHandler, filepath, fileargs);
          return;
        }
    handleFile(defaultHandler, filepath, fileargs);
  }

  private static void handleFile(final IFileHandler fileHandler, final String filepath, final String[] fileargs) {
    ZephyrCore.start(new Runnable() {
      @Override
      public void run() {
        try {
          fileHandler.handle(filepath, fileargs);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private static void displayFileNotFoundMessage(String filepath) {
    final String message = String.format("Cannot open %s", filepath);
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Loading", message);
      }
    });
  }

  public static String[] getExtensions() {
    loadFileHandlersIFN();
    List<String> extensions = new ArrayList<String>();
    for (IFileHandler fileHandler : fileHandlers)
      for (String extension : fileHandler.extensions())
        extensions.add("*." + extension);
    extensions.add("*.*");
    String[] result = new String[extensions.size()];
    extensions.toArray(result);
    return result;
  }
}
