package zephyr.plugin.core.api.monitoring.fileloggers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class AbstractFileLogger {

  public static final String TEMP = ".tmp";
  public final String filepath;
  protected PrintWriter file;
  protected final boolean temporaryFile;

  public AbstractFileLogger(String filepath, boolean temporaryFile) throws FileNotFoundException {
    this.filepath = filepath;
    checkFolders();
    this.temporaryFile = temporaryFile;
    String fileCreatedPath = temporaryFile ? filepath + TEMP : filepath;
    FileOutputStream newFile = new FileOutputStream(fileCreatedPath);
    file = new PrintWriter(newFile);
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

  static public boolean exist(String filename) {
    return new File(filename).canRead();
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
