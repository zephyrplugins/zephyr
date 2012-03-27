package zephyr.plugin.core.api.internal.logfiles;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipInputStream;

public class ZippedLogFile extends LogFile {

  private ZipInputStream zfile;

  public ZippedLogFile(String filepath) throws IOException {
    super(filepath);
  }

  @Override
  protected BufferedReader getReader(String filepath) throws IOException {
    FileInputStream finput = new FileInputStream(filepath);
    zfile = new ZipInputStream(new BufferedInputStream(finput));
    zfile.getNextEntry();
    return new BufferedReader(new InputStreamReader(zfile));
  }
}
