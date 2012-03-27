package zephyr.plugin.core.api.internal.logfiles;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class GZippedLogFile extends LogFile {

  private GZIPInputStream zfile;

  public GZippedLogFile(String filepath) throws IOException {
    super(filepath);
  }

  @Override
  protected BufferedReader getReader(String filepath) throws IOException {
    FileInputStream finput;
    finput = new FileInputStream(filepath);
    zfile = new GZIPInputStream(new BufferedInputStream(finput));
    return new BufferedReader(new InputStreamReader(zfile));
  }
}
