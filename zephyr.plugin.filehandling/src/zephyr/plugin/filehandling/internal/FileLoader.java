package zephyr.plugin.filehandling.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.api.internal.logfiles.LogFile;
import zephyr.plugin.filehandling.IFileHandler;


public class FileLoader {
  public static final String PluginID = "zephyr.plugin.filehandling";
  private static List<IFileHandler> fileHandlers = null;
  private static IFileHandler defaultHandler = new DefaultHandler();
  private static byte[] types = { Character.DECIMAL_DIGIT_NUMBER, Character.UPPERCASE_LETTER,
      Character.LOWERCASE_LETTER, Character.START_PUNCTUATION, Character.END_PUNCTUATION,
      Character.OTHER_PUNCTUATION, Character.MATH_SYMBOL, Character.DASH_PUNCTUATION,
      Character.CURRENCY_SYMBOL, Character.MODIFIER_SYMBOL, Character.CONNECTOR_PUNCTUATION };

  private FileLoader() {
  }

  static synchronized private void loadFileHandlersIFN() {
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
    if (!isBinary(filepath))
      handleFile(defaultHandler, filepath, fileargs);
  }

  private static boolean isBinary(String filepath) {
    LogFile logFile = LogFile.load(filepath);
    for (String label : logFile.labels())
      for (int i = 0; i < label.length(); i++) {
        boolean found = false;
        int type = Character.getType(label.charAt(i));
        for (byte t : types)
          if (t == type) {
            found = true;
            break;
          }
        if (!found)
          return true;
      }
    return false;
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
    Set<String> extensions = new HashSet<String>();
    for (IFileHandler fileHandler : fileHandlers)
      for (String extension : fileHandler.extensions())
        extensions.add("*." + toDialogExtension(extension));
    for (String extension : defaultHandler.extensions())
      extensions.add("*." + toDialogExtension(extension));
    extensions.add("*.*");
    String[] result = new String[extensions.size()];
    extensions.toArray(result);
    return result;
  }

  protected static String toDialogExtension(String extension) {
    int separatorPosition = extension.lastIndexOf('.');
    if (separatorPosition < 0)
      return extension;
    return extension.substring(separatorPosition + 1);
  }
}
