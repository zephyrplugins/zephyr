package zephyr.plugin.filehandling;

import java.io.IOException;
import java.util.List;

public interface IFileHandler {
  public static final String ID = "zephyr.plugin.filehandling.filehandler";

  void handle(String filepath, String[] fileargs) throws IOException;

  List<String> extensions();
}
