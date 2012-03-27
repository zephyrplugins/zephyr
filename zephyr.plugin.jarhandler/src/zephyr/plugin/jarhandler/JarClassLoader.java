package zephyr.plugin.jarhandler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import zephyr.plugin.core.ZephyrCore;

public class JarClassLoader extends ClassLoader {
  private final Map<String, Class<? extends Object>> classes = new Hashtable<String, Class<? extends Object>>();
  private final List<JarClassLoader> libraries = new ArrayList<JarClassLoader>();
  private char classNameReplacementChar;
  final private JarResources jarResources;
  private final Manifest manifest;
  private final JarClassLoader root;

  public JarClassLoader(String jarName) {
    this(null, new File(jarName));
  }

  public JarClassLoader(JarClassLoader root, File jarFile) {
    jarResources = new JarResources(jarFile);
    this.root = root;
    manifest = jarResources.getManifest();
    loadJarLibraries();
  }

  private void loadJarLibraries() {
    if (manifest == null)
      return;
    String librariesPath = manifest.getMainAttributes().getValue("Class-Path");
    if (librariesPath == null)
      return;
    for (File file : extractFiles(librariesPath))
      if (file.canRead() && file.isFile())
        libraries.add(new JarClassLoader(root == null ? this : root, file));
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
    return loadClass(className, false);
  }

  private Class<? extends Object> loadZephyrFrameworkClass(String className) {
    try {
      return ZephyrCore.classLoader().loadClass(className);
    } catch (ClassNotFoundException e) {
    }
    return null;
  }

  private Class<? extends Object> loadLibraryClass(String className) {
    for (JarClassLoader classLoader : libraries)
      try {
        if (classLoader == this)
          continue;
        return classLoader.loadClassInternal(className, true);
      } catch (ClassNotFoundException e) {
      } catch (NoClassDefFoundError e) {
      }
    return null;
  }

  protected Package definePackage(String name)
      throws IllegalArgumentException {
    String path = name.replace('.', '/').concat("/");
    String specTitle = null, specVersion = null, specVendor = null;
    String implTitle = null, implVersion = null, implVendor = null;
    URL sealBase = null;

    Attributes attributes = manifest != null ? manifest.getAttributes(path) : null;
    if (attributes != null) {
      specTitle = attributes.getValue(Name.SPECIFICATION_TITLE);
      specVersion = attributes.getValue(Name.SPECIFICATION_VERSION);
      specVendor = attributes.getValue(Name.SPECIFICATION_VENDOR);
      implTitle = attributes.getValue(Name.IMPLEMENTATION_TITLE);
      implVersion = attributes.getValue(Name.IMPLEMENTATION_VERSION);
      implVendor = attributes.getValue(Name.IMPLEMENTATION_VENDOR);
    }
    attributes = manifest != null ? manifest.getMainAttributes() : null;
    if (attributes != null) {
      if (specTitle == null)
        specTitle = attributes.getValue(Name.SPECIFICATION_TITLE);
      if (specVersion == null)
        specVersion = attributes.getValue(Name.SPECIFICATION_VERSION);
      if (specVendor == null)
        specVendor = attributes.getValue(Name.SPECIFICATION_VENDOR);
      if (implTitle == null)
        implTitle = attributes.getValue(Name.IMPLEMENTATION_TITLE);
      if (implVersion == null)
        implVersion = attributes.getValue(Name.IMPLEMENTATION_VERSION);
      if (implVendor == null)
        implVendor = attributes.getValue(Name.IMPLEMENTATION_VENDOR);
    }
    return definePackage(name, specTitle, specVersion, specVendor,
                         implTitle, implVersion, implVendor, sealBase);
  }

  @Override
  public synchronized Class<? extends Object> loadClass(String className, boolean resolveIt)
      throws ClassNotFoundException {
    if (root != null)
      return root.loadClassInternal(className, resolveIt);
    return loadClassInternal(className, resolveIt);
  }

  public synchronized Class<? extends Object> loadClassInternal(String className, boolean resolveIt)
      throws ClassNotFoundException {

    Class<? extends Object> result = classes.get(className);
    if (result != null)
      return result;

    result = loadZephyrFrameworkClass(className);
    if (result != null)
      return result;

    result = loadClassFromSelf(className, resolveIt);
    if (result != null)
      return result;

    result = loadLibraryClass(className);
    if (result != null)
      return result;

    throw new ClassNotFoundException();
  }

  private Class<? extends Object> linkClass(String className, Class<? extends Object> result, boolean resolveIt) {
    if (!resolveIt)
      return result;

    int lastDotIndex = className.lastIndexOf('.');
    if (lastDotIndex != -1) {
      String packageName = className.substring(0, lastDotIndex);
      Package pack = getPackage(packageName);
      if (pack == null)
        definePackage(packageName);
    }

    resolveClass(result);

    assert result.getPackage() != null;
    classes.put(className, result);
    return result;
  }

  private Class<? extends Object> loadClassFromSelf(String className, boolean resolveIt) throws ClassFormatError {
    Class<? extends Object> result;
    byte[] classBytes = loadClassBytes(className);
    if (classBytes == null)
      return null;
    result = defineClass(className, classBytes, 0, classBytes.length);
    if (result == null)
      throw new ClassFormatError();
    return linkClass(className, result, resolveIt);
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
    return jarResources.getResource(formattedClassName + ".class");
  }

  public JarResources jar() {
    return jarResources;
  }

  @Override
  public String toString() {
    return String.format("jarloader<%s>", jarResources.jarFile.getAbsolutePath());
  }
}
