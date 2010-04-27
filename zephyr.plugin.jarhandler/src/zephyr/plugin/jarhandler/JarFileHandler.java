package zephyr.plugin.jarhandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import rlpark.plugin.utils.Utils;
import rlpark.plugin.utils.time.Timed;
import zephyr.ZephyrPlotting;
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
    InputStream is = jarLoader.jar().getResourceAsStream("META-INF/MANIFEST.MF");
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line;
    try {
      line = reader.readLine();
      while (line != null) {
        if (line.startsWith("Main-Class")) {
          reader.close();
          return extractClassName(line);
        }
        line = reader.readLine();
      }
      reader.close();
      throw new RuntimeException("Cannot find class to load");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String extractClassName(String line) {
    String className = line.split(":")[1];
    while (className.charAt(0) == ' ')
      className = className.substring(1);
    return className;
  }
}
