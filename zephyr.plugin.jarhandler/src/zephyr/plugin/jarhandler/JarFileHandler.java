package zephyr.plugin.jarhandler;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.jar.Manifest;

import zephyr.plugin.core.Utils;
import zephyr.plugin.filehandling.IFileHandler;

public class JarFileHandler implements IFileHandler {
  public JarFileHandler() {
  }

  @Override
  public List<String> extensions() {
    return Utils.asList("jar");
  }

  @Override
  public void handle(final String filepath, String[] fileargs) throws IOException {
    JarClassLoader jarLoader = AccessController.doPrivileged(new PrivilegedAction<JarClassLoader>() {
      @Override
      public JarClassLoader run() {
        return new JarClassLoader(filepath);
      }
    });
    String className = fileargs.length == 1 ? fileargs[0] : retrieveClassName(jarLoader);
    if (className == null)
      throw new RuntimeException("Cannot find the main class to load");
    Object mainObject;
    try {
      mainObject = jarLoader.loadClass(className, true).newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (!(mainObject instanceof Runnable))
      throw new RuntimeException(mainObject.getClass().getCanonicalName() + " must implement "
          + Runnable.class.getCanonicalName());
    ((Runnable) mainObject).run();
  }

  private String retrieveClassName(JarClassLoader jarLoader) {
    Manifest manifest = jarLoader.jar().getManifest();
    if (manifest == null)
      return null;
    return manifest.getMainAttributes().getValue("Main-Class");
  }
}
