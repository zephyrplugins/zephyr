package zephyr.plugin.jarhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import zephyr.ZephyrPlotting;
import zephyr.plugin.core.Utils;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Timed;
import zephyr.plugin.filehandling.IFileHandler;

public class JarFileHandler implements IFileHandler {
  public class JarRunnable {
    final Runnable runnable;
    final Clock clock;
    private final String filename;

    public JarRunnable(String filename, Object object) {
      runnable = (Runnable) object;
      clock = ((Timed) object).clock();
      this.filename = filename;
    }

    public String clockName() {
      return runnable.getClass().getSimpleName();
    }

    public void run() {
      runnable.run();
    }

    public String filename() {
      return filename;
    }
  }

  static protected final List<JarRunnable> jars = new ArrayList<JarRunnable>();

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
      throw new RuntimeException("Cannot find the main class to load");
    Object mainObject;
    try {
      mainObject = jarLoader.loadClass(className, true).newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (!(mainObject instanceof Timed))
      throw new RuntimeException(mainObject.getClass().getCanonicalName() + " must implement "
          + Timed.class.getCanonicalName());
    if (!(mainObject instanceof Runnable))
      throw new RuntimeException(mainObject.getClass().getCanonicalName() + " must implement "
          + Runnable.class.getCanonicalName());
    JarRunnable jarRunnable = new JarRunnable(filepath, mainObject);
    jars.add(jarRunnable);
    Zephyr.advertise(jarRunnable.clock, jarRunnable, jarRunnable.clockName());
    ZephyrPlotting.createLogger(jarRunnable.clockName(), jarRunnable.clock).add(jarRunnable.runnable);
    jarRunnable.run();
  }

  private String retrieveClassName(JarClassLoader jarLoader) {
    Manifest manifest = jarLoader.jar().getManifest();
    if (manifest == null)
      return null;
    return manifest.getMainAttributes().getValue("Main-Class");
  }
}
