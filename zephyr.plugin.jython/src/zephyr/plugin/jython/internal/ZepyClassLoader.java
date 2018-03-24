package zephyr.plugin.jython.internal;

import java.util.HashSet;
import java.util.Set;
import org.python.core.imp;
import zephyr.plugin.core.ZephyrCore;

public class ZepyClassLoader extends ClassLoader {
  private final Set<String> resolving = new HashSet<String>();

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    if (resolving.contains(name))
      return null;
    resolving.add(name);
    ClassLoader syspathJavaLoader = imp.getSyspathJavaLoader();
    Class<?> result = null;
    try {
      result = syspathJavaLoader.loadClass(name);
    } catch (ClassNotFoundException e) {
    }
    if (result != null)
      return resolve(name, result);
    try {
      result = ZephyrCore.classLoader().loadClass(name);
    } catch (ClassNotFoundException e) {
    }
    return resolve(name, result);
  }

  private Class<?> resolve(String name, Class<?> result) throws ClassNotFoundException {
    resolving.remove(name);
    if (result == null)
      throw new ClassNotFoundException(name);
    return result;
  }
}
