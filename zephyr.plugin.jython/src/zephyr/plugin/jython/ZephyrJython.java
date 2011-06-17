package zephyr.plugin.jython;

import zephyr.plugin.jython.internal.JythonFileHandler;

public class ZephyrJython {
  static public void runFile(String filepath) {
    runFile(filepath, new String[] {});
  }

  static public void runFile(String filepath, String[] fileargs) {
    JythonFileHandler.runFile(filepath, fileargs);
  }
}
