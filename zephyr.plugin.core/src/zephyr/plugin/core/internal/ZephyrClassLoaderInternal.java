package zephyr.plugin.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;


public class ZephyrClassLoaderInternal extends ClassLoader {
  private List<Bundle> zephyrClasspathBundles = null;

  private Class<? extends Object> superFindSystemClass(String className) {
    try {
      return super.findSystemClass(className);
    } catch (ClassNotFoundException e) {
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    Class<? extends Object> result = superFindSystemClass(name);
    if (result != null)
      return result;
    if (zephyrClasspathBundles == null)
      zephyrClasspathBundles = initializeZephyrClasspathBundles();
    for (Bundle bundle : zephyrClasspathBundles) {
      try {
        result = bundle.loadClass(name);
      } catch (ClassNotFoundException e) {
      }
      if (result != null)
        return result;
    }
    throw new ClassNotFoundException(name);
  }

  private List<Bundle> initializeZephyrClasspathBundles() {
    List<Bundle> loaders = new ArrayList<Bundle>();
    IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("zephyr.classpath");
    for (IConfigurationElement element : config)
      loaders.add(Platform.getBundle(element.getContributor().getName()));
    loaders.add(Platform.getBundle("zephyr.plugin.core.api"));
    return loaders;
  }
}
