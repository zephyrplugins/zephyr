package zephyr.plugin.clojure.internal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import zephyr.plugin.core.Utils;
import zephyr.plugin.core.utils.ZephyrClassLoader;
import zephyr.plugin.filehandling.IFileHandler;
import clojure.lang.Compiler;
import clojure.lang.RT;
import clojure.lang.Var;

public class ClojureFileHandler implements IFileHandler {
  public ClojureFileHandler() {
  }

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    setClassLoader();
    try {
      Compiler.loadFile(filepath);
      Var main = RT.var(extractNamespace(filepath), "main");
      Object result = main.invoke(fileargs);
      System.out.println(String.valueOf(result));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setClassLoader() {
    ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
    ZephyrClassLoader zephyrClassLoader = new ZephyrClassLoader(parentClassLoader);
    Thread.currentThread().setContextClassLoader(zephyrClassLoader);
  }

  private String extractNamespace(String filepath) {
    String fileName = new File(filepath).getName();
    int extensionIndex = fileName.indexOf(".");
    return extensionIndex != -1 ? fileName.substring(0, extensionIndex) : fileName;
  }

  @Override
  public List<String> extensions() {
    return Utils.asList("clj");
  }
}
