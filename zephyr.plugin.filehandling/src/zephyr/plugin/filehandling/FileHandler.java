package zephyr.plugin.filehandling;

import zephyr.plugin.filehandling.internal.FileLoader;

public class FileHandler {
  static public void openFile(String filepath) {
    FileLoader.openFile(filepath);
  }

  static public void openFile(String filepath, String[] fileargs) {
    FileLoader.openFile(filepath, fileargs);
  }
}
