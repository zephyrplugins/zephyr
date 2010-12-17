package zephyr.plugin.core.api.logfiles;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextLogFile extends LogFile {

  public TextLogFile(String filepath) throws IOException {
    super(filepath);
  }

  @Override
  protected BufferedReader getReader(String filepath) throws IOException {
    FileInputStream finput = new FileInputStream(filepath);
    InputStreamReader inputStream = new InputStreamReader(finput);
    return new BufferedReader(inputStream);
  }
}
