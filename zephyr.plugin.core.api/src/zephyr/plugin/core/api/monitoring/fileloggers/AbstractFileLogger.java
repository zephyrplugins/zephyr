package zephyr.plugin.core.api.monitoring.fileloggers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.zip.GZIPOutputStream;

public class AbstractFileLogger {
  public static final String TEMP = ".tmp";
  public static final String GZEXT = ".gz";
  public final String filepath;
  protected PrintWriter file;
  protected final boolean temporaryFile;

  public AbstractFileLogger(String filepath, boolean temporaryFile, boolean compress) throws IOException {
    this.filepath = compress ? filepath + GZEXT : filepath;
    checkFolders();
    this.temporaryFile = temporaryFile;
    String fileCreatedPath = temporaryFile ? this.filepath + TEMP : this.filepath;
    OutputStream outputStream = createOutputStream(fileCreatedPath, compress);
    file = new PrintWriter(outputStream, true);
  }

  protected OutputStream createOutputStream(String fileCreatedPath, boolean compress) throws IOException {
    OutputStream fileOutputStream = new FileOutputStream(fileCreatedPath);
    if (!compress)
      return fileOutputStream;
    return new GZIPOutputStream(fileOutputStream);
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

  static public boolean exist(String filepath) {
    String[] possibilities = new String[] { filepath, filepath + TEMP, filepath + GZEXT, filepath + GZEXT + TEMP };
    for (String possibility : possibilities)
      if (new File(possibility).canRead())
        return true;
    return false;
  }

  public void close() {
    file.flush();
    file.close();
    file = null;
    if (temporaryFile) {
      File finalFile = new File(filepath + TEMP);
      if (!finalFile.renameTo(new File(filepath)))
        throw new RuntimeException("Error renaming to " + filepath);
    }
  }
}
