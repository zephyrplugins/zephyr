package zephyr.plugin.core.api.internal.logfiles;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import zephyr.plugin.core.api.internal.bz2.CBZip2InputStream;

public class BZippedLogFile extends LogFile {
  private CBZip2InputStream zfile;

  public BZippedLogFile(String filepath) throws IOException {
    super(filepath);
  }

  @SuppressWarnings("resource")
  @Override
  protected BufferedReader getReader(String filepath) throws IOException {
    FileInputStream finput;
    finput = new FileInputStream(filepath);
    zfile = new CBZip2InputStream(new BufferedInputStream(finput));
    return new BufferedReader(new InputStreamReader(zfile));
  }
}
