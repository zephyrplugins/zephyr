package zephyr.plugin.jarhandler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.jar.Manifest;

import zephyr.plugin.core.utils.Misc;
import zephyr.plugin.filehandling.IFileHandler;

public class JarFileHandler implements IFileHandler {
  public JarFileHandler() {
  }

  @Override
  public List<String> extensions() {
    return Misc.asList("jar");
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
    Method mainMethod = retrieveMainMethod(jarLoader, className);
    try {
      Object arg = fileargs;
      mainMethod.invoke(null, arg);
    } catch (IllegalArgumentException e1) {
      e1.printStackTrace();
    } catch (IllegalAccessException e1) {
      e1.printStackTrace();
    } catch (InvocationTargetException e1) {
      e1.printStackTrace();
    }
  }

  private Method retrieveMainMethod(JarClassLoader jarLoader, String className) {
    Method method = null;
    try {
      method = jarLoader.loadClass(className, true).getMethod("main", String[].class);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    if (method == null)
      return null;
    if (!Modifier.isPublic(method.getModifiers())) {
      System.err.println("error: main method is not public");
      return null;
    }
    if (!Modifier.isStatic(method.getModifiers())) {
      System.err.println("error: main method is not static");
      return null;
    }
    if (!method.getReturnType().equals(void.class)) {
      System.err.println("error: main method does not return 'int'");
      return null;
    }
    return method;
  }

  private String retrieveClassName(JarClassLoader jarLoader) {
    Manifest manifest = jarLoader.jar().getManifest();
    if (manifest == null)
      return null;
    return manifest.getMainAttributes().getValue("Main-Class");
  }
}
