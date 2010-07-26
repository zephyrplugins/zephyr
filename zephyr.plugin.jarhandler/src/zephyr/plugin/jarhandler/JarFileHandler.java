package zephyr.plugin.jarhandler;

import java.io.IOException;
import java.util.List;
import java.util.jar.Manifest;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.Utils;
import zephyr.plugin.core.api.synchronization.Timed;
import zephyr.plugin.filehandling.IFileHandler;

public class JarFileHandler implements IFileHandler {

  public JarFileHandler() {
  }

  @Override
  public List<String> extensions() {
    return Utils.asList("jar");
  }

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    JarClassLoader jarLoader = new JarClassLoader(filepath);
    String className = fileargs.length == 1 ? fileargs[0] : retrieveClassName(jarLoader);
    if (className == null)
      throw new RuntimeException("Cannot find class to load");
    Object mainObject;
    try {
      mainObject = jarLoader.loadClass(className, true).newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    final String clockName = mainObject.getClass().getSimpleName();
    ZephyrPlotting.createLogger(clockName, ((Timed) mainObject).clock()).add(mainObject);
    ((Runnable) mainObject).run();
  }

  private String retrieveClassName(JarClassLoader jarLoader) {
    Manifest manifest = jarLoader.jar().getManifest();
    if (manifest == null)
      return null;
    return manifest.getMainAttributes().getValue("Main-Class");
  }
}
