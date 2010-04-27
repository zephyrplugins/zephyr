package zephyr.plugin.jarhandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import zephyr.plugin.common.ZephyrPluginCommon;

public class JarClassLoader extends ClassLoader {

  private final Map<String, Class<? extends Object>> classes = new Hashtable<String, Class<? extends Object>>();
  private final List<JarClassLoader> libraries = new ArrayList<JarClassLoader>();
  private char classNameReplacementChar;
  final private JarResources jarResources;

  public JarClassLoader(String jarName) {
    this(new File(jarName));
  }

  public JarClassLoader(File jarFile) {
    jarResources = new JarResources(jarFile);
    loadJarLibraries();
  }

  private void loadJarLibraries() {
    Manifest manifest = jarResources.getManifest();
    if (manifest == null)
      return;
    String librariesPath = manifest.getMainAttributes().getValue("Class-Path");
    if (librariesPath == null)
      return;
    for (File file : extractFiles(librariesPath))
      if (file.canRead() && file.isFile())
        libraries.add(new JarClassLoader(file));
  }

  private List<File> extractFiles(String librariesPath) {
    File libRootFolder = jarResources.jarFile.getParentFile();
    String[] libraryPaths = librariesPath.split(" ");
    List<File> result = new ArrayList<File>();
    for (String libraryPath : libraryPaths)
      result.add(new File(libRootFolder.getAbsolutePath() + "/" + libraryPath));
    return result;
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

  private Class<? extends Object> loadLibraryClass(String className, boolean resolveIt) {
    for (JarClassLoader classLoader : libraries)
      try {
        return classLoader.loadClass(className, resolveIt);
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

    result = loadLibraryClass(className, resolveIt);
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
