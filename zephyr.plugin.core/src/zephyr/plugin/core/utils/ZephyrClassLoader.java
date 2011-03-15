package zephyr.plugin.core.utils;

import zephyr.ZephyrCore;

public class ZephyrClassLoader extends ClassLoader {
  public ZephyrClassLoader(ClassLoader parent) {
    super(parent);
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    Class<?> result = null;
    try {
      result = super.findClass(name);
    } catch (ClassNotFoundException e) {
    }
    if (result != null)
      return result;
    try {
      result = ZephyrCore.classLoader().loadClass(name);
    } catch (ClassNotFoundException e) {
    }
    return result;
  }
}
