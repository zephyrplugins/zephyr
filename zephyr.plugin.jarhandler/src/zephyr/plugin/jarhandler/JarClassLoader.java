package zephyr.plugin.jarhandler;

import java.util.Hashtable;
import java.util.Map;

import zephyr.plugin.common.ZephyrPluginCommon;

public class JarClassLoader extends ClassLoader {

  private final Map<String, Class<? extends Object>> classes = new Hashtable<String, Class<? extends Object>>();
  private char classNameReplacementChar;
  final private JarResources jarResources;

  public JarClassLoader(String jarName) {
    jarResources = new JarResources(jarName);
  }

  @Override
  public Class<? extends Object> loadClass(String className) throws ClassNotFoundException {
    return (loadClass(className, true));
  }

  private Class<? extends Object> loadSystemClass(String className) {
    try {
      return super.findSystemClass(className);
    } catch (ClassNotFoundException e) {
    }
    return null;
  }

  private Class<? extends Object> loadPluginClass(String className) {
    try {
      return ZephyrPluginCommon.getDefault().loadClass(className);
    } catch (ClassNotFoundException e) {
    }
    return null;
  }

  @Override
  public synchronized Class<? extends Object> loadClass(String className,
        boolean resolveIt) throws ClassNotFoundException {
    Class<? extends Object> result = classes.get(className);
    if (result != null)
      return result;

    result = loadSystemClass(className);
    if (result != null)
      return result;

    result = loadPluginClass(className);
    if (result != null)
      return result;

    byte[] classBytes = loadClassBytes(className);
    if (classBytes == null)
      throw new ClassNotFoundException();

    result = defineClass(className, classBytes, 0, classBytes.length);
    if (result == null)
      throw new ClassFormatError();

    if (resolveIt)
      resolveClass(result);

    classes.put(className, result);
    return result;
  }

  public void setClassNameReplacementChar(char replacement) {
    classNameReplacementChar = replacement;
  }

  protected byte[] loadClassBytes(String className) {
    String formattedClassName;
    if (classNameReplacementChar == '\u0000')
      formattedClassName = className.replace('.', '/');
    else
      formattedClassName = className.replace('.', classNameReplacementChar);
    return (jarResources.getResource(formattedClassName + ".class"));
  }

  public JarResources jar() {
    return jarResources;
  }
}
