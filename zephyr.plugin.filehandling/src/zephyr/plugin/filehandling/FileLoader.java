package zephyr.plugin.filehandling;

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

import zephyr.Zephyr;


public class FileLoader {
  private List<IFileHandler> fileHandlers = null;

  public FileLoader() {
  }

  private void loadFileHandlersIFN() {
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

  public void openFile(String filepath) {
    loadFileHandlersIFN();
    if (!new File(filepath).canRead()) {
      displayFileNotFoundMessage(filepath);
      return;
    }
    for (IFileHandler fileHandler : fileHandlers)
      for (String extension : fileHandler.extensions())
        if (filepath.endsWith(extension)) {
          handleFile(filepath, fileHandler);
          return;
        }
  }

  private void handleFile(final String filepath, final IFileHandler fileHandler) {
    Zephyr.start(new Runnable() {
      @Override
      public void run() {
        try {
          fileHandler.handle(filepath);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void displayFileNotFoundMessage(String filepath) {
    final String message = String.format("Cannot open %s", filepath);
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Loading", message);
      }
    });
  }

  public String[] getExtensions() {
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
