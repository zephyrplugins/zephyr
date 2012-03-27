package zephyr.plugin.core.api.internal.monitoring.fileloggers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.zip.GZIPOutputStream;

import zephyr.plugin.core.api.internal.monitoring.helpers.Loggers;

public class AbstractFileLogger {
  public static final String TEMP = ".tmp";
  public static final String GZEXT = ".gz";
  public final String filepath;
  protected PrintWriter file;
  protected final boolean temporaryFile;

  public AbstractFileLogger(String filepath, boolean temporaryFile) throws IOException {
    this.filepath = filepath;
    checkFolders();
    this.temporaryFile = temporaryFile;
    String fileCreatedPath = temporaryFile ? this.filepath + TEMP : this.filepath;
    OutputStream outputStream = createOutputStream(fileCreatedPath);
    file = new PrintWriter(outputStream, true);
  }

  protected OutputStream createOutputStream(String fileCreatedPath) throws IOException {
    OutputStream out = new FileOutputStream(fileCreatedPath);
    if (fileCreatedPath.endsWith(GZEXT))
      out = new GZIPOutputStream(out);
    return new BufferedOutputStream(out);
  }

  private void checkFolders() {
    File logFile = new File(filepath);
    File folder = logFile.getParentFile();
    if (!folder.isDirectory() && !folder.mkdirs())
      throw new RuntimeException("Could not create folder");
  }

  public AbstractFileLogger(Writer writer) {
    file = new PrintWriter(writer);
    temporaryFile = false;
    filepath = null;
  }

  public void close() {
    file.flush();
    file.close();
    file = null;
    if (temporaryFile) {
      finaliseFile(filepath);
    }
  }

  static private File currentFilename(String filepath) {
    String[] possibilities = new String[] { filepath + TEMP, filepath };
    for (String possibility : possibilities) {
      File possibleFile = new File(possibility);
      if (possibleFile.canRead())
        return possibleFile;
    }
    return null;
  }

  static public boolean exist(String filepath) {
    return currentFilename(filepath) != null;
  }

  static public boolean isTemporary(String filepath) {
    File possibleFile = currentFilename(filepath);
    if (possibleFile == null)
      return false;
    return possibleFile.getAbsolutePath().endsWith(TEMP);
  }

  public static String makeTemporary(String filepath) throws IOException {
    assert !isTemporary(filepath);
    File currentFileName = currentFilename(filepath);
    if (currentFileName == null) {
      String temporaryFilepath = filepath + TEMP;
      File temporaryFile = new File(temporaryFilepath);
      File parentFolder = new File(temporaryFile.getParent());
      if (!parentFolder.canRead())
        parentFolder.mkdirs();
      if (!temporaryFile.createNewFile())
        throw new RuntimeException("Error creating " + temporaryFilepath);
      return temporaryFilepath;
    }
    File resultFile = new File(currentFileName.getAbsolutePath() + TEMP);
    Loggers.copyFile(currentFileName, resultFile);
    return resultFile.getAbsolutePath();
  }

  static public void finaliseFile(String filepath) {
    File currentFile = currentFilename(filepath);
    String path = currentFile.getAbsolutePath();
    assert path.endsWith(TEMP);
    File finalFile = new File(path.substring(0, path.length() - TEMP.length()));
    if (!currentFile.renameTo(finalFile))
      throw new RuntimeException("Error renaming to " + filepath);
  }
}
